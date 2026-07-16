package com.github.jarva.arsadditions.common.entity;

import com.github.jarva.arsadditions.common.item.data.MagicCarpetInventoryData;
import com.github.jarva.arsadditions.common.perk.CarpetInventoryPerk;
import com.github.jarva.arsadditions.common.perk.CarpetSizePerk;
import com.github.jarva.arsadditions.common.perk.CarpetSpeedPerk;
import com.github.jarva.arsadditions.mixin.LivingEntityAccessor;
import com.github.jarva.arsadditions.setup.registry.AddonDataComponentRegistry;
import com.github.jarva.arsadditions.setup.registry.AddonEntityRegistry;
import com.github.jarva.arsadditions.setup.registry.AddonItemRegistry;
import com.hollingsworth.arsnouveau.api.perk.IPerk;
import com.hollingsworth.arsnouveau.api.perk.PerkInstance;
import com.hollingsworth.arsnouveau.api.perk.PerkSlot;
import com.hollingsworth.arsnouveau.api.util.PerkUtil;
import com.hollingsworth.arsnouveau.common.items.data.ArmorPerkHolder;
import com.hollingsworth.arsnouveau.setup.registry.DataComponentRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.DismountHelper;
import net.minecraft.world.entity.vehicle.VehicleEntity;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.SimpleMenuProvider;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.List;
import java.util.UUID;

public class MagicCarpetEntity extends VehicleEntity implements GeoEntity {
    private static final String OWNER_UUID_TAG = "OwnerUUID";
    private static final String CARPET_STACK_TAG = "CarpetStack";
    private static final EntityDataAccessor<Float> DATA_ID_SIDE_TILT = SynchedEntityData.defineId(MagicCarpetEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<ItemStack> DATA_ID_CARPET_STACK = SynchedEntityData.defineId(MagicCarpetEntity.class, EntityDataSerializers.ITEM_STACK);

    private static final int SMALL_MAX_PASSENGERS = 2;
    private static final int LARGE_MAX_PASSENGERS = 5;
    private static final float SMALL_FRONT_PASSENGER_Z = 0.2F;
    private static final float SMALL_BACK_PASSENGER_Z = -0.6F;
    private static final float LARGE_DRIVER_SEAT_X = 0.0F;
    private static final float LARGE_DRIVER_SEAT_Z = 14.0F / 16.0F;
    private static final float LARGE_PASSENGER_X_SPREAD = 10.0F / 16.0F;
    private static final float LARGE_PASSENGER_FRONT_ROW_Z = 2.0F / 16.0F;
    private static final float LARGE_PASSENGER_BACK_ROW_Z = -8.0F / 16.0F;
    private static final double PASSENGER_HEIGHT_OFFSET = 0.08D;
    private static final double DISMOUNT_TOP_OFFSET = 0.08D;
    private static final float MAX_SIDE_TILT = 18.0F;
    private static final float SIDE_TILT_LERP = 0.25F;
    private static final double SMALL_HITBOX_HALF_WIDTH = 12.0D / 16.0D;
    private static final double SMALL_HITBOX_HALF_LENGTH = 16.0D / 16.0D;
    private static final double LARGE_HITBOX_HALF_WIDTH = 24.0D / 16.0D;
    private static final double LARGE_HITBOX_HALF_LENGTH = 32.0D / 16.0D;
    private static final double HITBOX_HEIGHT = 1.0D / 16.0D;
    private static final double SMALL_HITBOX_LOCAL_X_OFFSET = 0.0D;
    private static final double SMALL_HITBOX_LOCAL_Z_OFFSET = -1.0D / 16.0D;
    private static final double LARGE_HITBOX_LOCAL_X_OFFSET = 0.0D;
    private static final double LARGE_HITBOX_LOCAL_Z_OFFSET = 0.0D;
    private static final double HITBOX_ROTATION_CLEARANCE = 1.0D / 16.0D;
    private static final float SMALL_SHADOW_RADIUS = 0.8F;
    private static final float LARGE_SHADOW_RADIUS = 1.2F;
    private static final double SMALL_MODEL_HALF_LENGTH = 1.0D;
    private static final double SMALL_MODEL_HALF_WIDTH = 0.75D;
    private static final double LARGE_MODEL_HALF_LENGTH = 32.0D / 16.0D;
    private static final double LARGE_MODEL_HALF_WIDTH = 24.0D / 16.0D;

    private static final double MAX_HORIZONTAL_SPEED = 0.90D;
    private static final double MAX_VERTICAL_SPEED = 0.30D;
    private static final double HORIZONTAL_ACCELERATION = 0.045D;
    private static final double VERTICAL_ACCELERATION = 0.05D;
    private static final double SPEED_THREAD_BONUS_PER_SLOT = 0.15D;
    private static final double SUMMON_STANDOFF_DISTANCE = 5.0D;
    private static final double SUMMON_STANDOFF_TOLERANCE = 0.35D;
    private static final double SUMMON_VERTICAL_ALIGN_EPSILON = 0.35D;
    private static final double SUMMON_VERTICAL_TRACK_FACTOR = 0.4D;
    private static final double SUMMON_APPROACH_SPEED = 0.72D;
    private static final double SUMMON_CORRECTION_SPEED = 0.45D;
    private static final double SUMMON_VERTICAL_SPEED = 0.22D;
    private static final double SUMMON_VELOCITY_LERP = 0.35D;
    private static final double SUMMON_PITCH_FACTOR = 16.0D;
    private static final int DESCEND_HOLD_TICKS = 5;
    private static final double RIDDEN_DRAG = 0.96D;
    private static final double VERTICAL_DRAG = 0.85D;
    private static final double IDLE_DRAG = 0.8D;
    private static final double IDLE_VERTICAL_DRAG = 0.65D;
    private static final double INVENTORY_INTERACTION_DISTANCE_SQR = 64.0D;
    private static final RawAnimation IDLE_ANIMATION = RawAnimation.begin().thenLoop("idle");
    private static final RawAnimation MOVE_ANIMATION = RawAnimation.begin().thenLoop("move");

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private int lerpSteps;
    private double lerpX;
    private double lerpY;
    private double lerpZ;
    private double lerpYRot;
    private double lerpXRot;
    private Direction hitboxFacing = Direction.NORTH;
    @Nullable
    private UUID ownerUUID;
    @Nullable
    private UUID summonTargetUUID;
    private int descendHoldTicks;
    private boolean descendInputActive;

    public MagicCarpetEntity(EntityType<? extends MagicCarpetEntity> entityType, Level level) {
        super(entityType, level);
        this.blocksBuilding = true;
        this.setNoGravity(true);
        this.hitboxFacing = Direction.fromYRot(this.getYRot());
    }

    public MagicCarpetEntity(Level level, double x, double y, double z) {
        this(AddonEntityRegistry.MAGIC_CARPET_ENTITY.get(), level);
        this.setPos(x, y, z);
        this.xo = x;
        this.yo = y;
        this.zo = z;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller", 2, this::animationPredicate));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_ID_SIDE_TILT, 0.0F);
        builder.define(DATA_ID_CARPET_STACK, createBaseCarpetStack());
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compound) {
        this.ownerUUID = compound.hasUUID(OWNER_UUID_TAG) ? compound.getUUID(OWNER_UUID_TAG) : null;
        if (compound.contains(CARPET_STACK_TAG)) {
            ItemStack stack = ItemStack.parse(this.registryAccess(), compound.getCompound(CARPET_STACK_TAG)).orElse(createBaseCarpetStack());
            this.setCarpetStack(stack);
        }
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compound) {
        if (this.ownerUUID != null) {
            compound.putUUID(OWNER_UUID_TAG, this.ownerUUID);
        }
        compound.put(CARPET_STACK_TAG, this.getCarpetStack().save(this.registryAccess()));
    }

    @Override
    public void tick() {
        super.tick();
        this.setNoGravity(true);
        this.tickLerp();
        if (!(this.getControllingPassenger() instanceof Player)) {
            this.descendHoldTicks = 0;
            this.descendInputActive = false;
        }

        if (this.isControlledByLocalInstance()) {
            LivingEntity controller = this.getControllingPassenger();
            if (controller != null) {
                this.applyControlledMovement(controller);
            } else if (this.hasSummonTarget()) {
                this.applySummonMovement();
            } else {
                this.applyIdleMovement();
            }

            this.move(MoverType.SELF, this.getDeltaMovement());
        } else {
            this.setDeltaMovement(Vec3.ZERO);
        }

        this.updateHitboxFacingSafely();
        this.checkInsideBlocks();
    }

    private void tickLerp() {
        if (this.isControlledByLocalInstance()) {
            this.lerpSteps = 0;
            this.syncPacketPositionCodec(this.getX(), this.getY(), this.getZ());
        }

        if (this.lerpSteps > 0) {
            this.lerpPositionAndRotationStep(this.lerpSteps, this.lerpX, this.lerpY, this.lerpZ, this.lerpYRot, this.lerpXRot);
            this.lerpSteps--;
        }
    }

    @Override
    public void lerpTo(double x, double y, double z, float yRot, float xRot, int steps) {
        this.lerpX = x;
        this.lerpY = y;
        this.lerpZ = z;
        this.lerpYRot = yRot;
        this.lerpXRot = xRot;
        this.lerpSteps = 10;
    }

    @Override
    public double lerpTargetX() {
        return this.lerpSteps > 0 ? this.lerpX : this.getX();
    }

    @Override
    public double lerpTargetY() {
        return this.lerpSteps > 0 ? this.lerpY : this.getY();
    }

    @Override
    public double lerpTargetZ() {
        return this.lerpSteps > 0 ? this.lerpZ : this.getZ();
    }

    @Override
    public float lerpTargetXRot() {
        return this.lerpSteps > 0 ? (float) this.lerpXRot : this.getXRot();
    }

    @Override
    public float lerpTargetYRot() {
        return this.lerpSteps > 0 ? (float) this.lerpYRot : this.getYRot();
    }

    private void applyControlledMovement(LivingEntity controller) {
        this.clearSummonTarget();
        if (!(controller instanceof Player player)) {
            this.applyIdleMovement();
            return;
        }

        player.setSprinting(false);

        float previousYaw = this.getYRot();
        float previousPitch = this.getXRot();
        this.setXRot(player.getXRot() * 0.5F);
        this.setYRot(player.getYRot());
        this.xRotO = previousPitch;
        this.yRotO = previousYaw;
        this.updateSideTilt(player.xxa);
        this.updateDescendInput(player);

        Vec3 riddenInput = this.getRiddenInput(player);
        Vec3 horizontalIntent = new Vec3(riddenInput.x, 0.0D, riddenInput.z);

        if (horizontalIntent.lengthSqr() > 1.0D) {
            horizontalIntent = horizontalIntent.normalize();
        }

        Vec3 worldIntent = this.toWorldIntent(horizontalIntent, player.getYRot());
        double verticalIntent = this.getVerticalIntent(player, horizontalIntent);
        double speedMultiplier = this.getSpeedMultiplier();

        Vec3 velocity = this.getDeltaMovement();
        // Scale acceleration with thread speed so boosted caps are actually reachable while riding.
        velocity = velocity.add(worldIntent.scale(HORIZONTAL_ACCELERATION * speedMultiplier));
        velocity = new Vec3(velocity.x, velocity.y + verticalIntent * VERTICAL_ACCELERATION, velocity.z);

        if (horizontalIntent.lengthSqr() < 1.0E-4D) {
            velocity = new Vec3(velocity.x * 0.75D, velocity.y, velocity.z * 0.75D);
        }

        velocity = this.clampVelocity(velocity);
        this.setDeltaMovement(velocity.multiply(RIDDEN_DRAG, VERTICAL_DRAG, RIDDEN_DRAG));
    }

    private double getVerticalIntent(Player player, Vec3 horizontalIntent) {
        if (this.isRiderJumping(player)) {
            return 1.0D;
        }
        if (this.isRiderDescending(player)) {
            return -1.0D;
        }
        if (Math.abs(horizontalIntent.z) > 0.01D) {
            return -Mth.sin(player.getXRot() * Mth.DEG_TO_RAD) * Math.abs(horizontalIntent.z);
        }
        return 0.0D;
    }

    private boolean isRiderJumping(LivingEntity rider) {
        return rider instanceof LivingEntityAccessor accessor && accessor.ars_additions$isJumping();
    }

    private boolean isRiderDescending(Player rider) {
        return this.descendInputActive && this.getControllingPassenger() == rider;
    }

    private void updateDescendInput(Player rider) {
        if (this.getControllingPassenger() != rider || !rider.isShiftKeyDown()) {
            this.descendHoldTicks = 0;
            this.descendInputActive = false;
            return;
        }

        this.descendHoldTicks = Math.min(this.descendHoldTicks + 1, DESCEND_HOLD_TICKS);
        this.descendInputActive = this.descendHoldTicks >= DESCEND_HOLD_TICKS;
    }

    private void applyIdleMovement() {
        float previousPitch = this.getXRot();
        this.setXRot(Mth.lerp(0.2F, this.getXRot(), 0.0F));
        this.xRotO = previousPitch;
        this.updateSideTilt(0.0F);

        Vec3 velocity = this.getDeltaMovement().multiply(IDLE_DRAG, IDLE_VERTICAL_DRAG, IDLE_DRAG);
        if (velocity.lengthSqr() < 1.0E-4D) {
            velocity = Vec3.ZERO;
        }
        this.setDeltaMovement(velocity);
    }

    private boolean hasSummonTarget() {
        return this.summonTargetUUID != null;
    }

    private void clearSummonTarget() {
        this.summonTargetUUID = null;
    }

    private void applySummonMovement() {
        Player target = this.resolveSummonTargetPlayer();
        if (target == null || !target.isAlive()) {
            this.clearSummonTarget();
            this.applyIdleMovement();
            return;
        }

        Vec3 carpetPos = this.position();
        double verticalDelta = target.getY() - carpetPos.y;
        Vec3 horizontalDelta = new Vec3(target.getX() - carpetPos.x, 0.0D, target.getZ() - carpetPos.z);
        double horizontalDistance = horizontalDelta.length();

        boolean verticalAligned = Math.abs(verticalDelta) <= SUMMON_VERTICAL_ALIGN_EPSILON;
        boolean inStandoffBand = horizontalDistance >= SUMMON_STANDOFF_DISTANCE - SUMMON_STANDOFF_TOLERANCE
                && horizontalDistance <= SUMMON_STANDOFF_DISTANCE + SUMMON_STANDOFF_TOLERANCE;
        if (verticalAligned && inStandoffBand) {
            this.clearSummonTarget();
            this.applyIdleMovement();
            return;
        }

        Vec3 horizontalIntent = Vec3.ZERO;
        double targetHorizontalSpeed = 0.0D;
        if (horizontalDistance > 1.0E-5D) {
            double minDistance = SUMMON_STANDOFF_DISTANCE - SUMMON_STANDOFF_TOLERANCE;
            double maxDistance = SUMMON_STANDOFF_DISTANCE + SUMMON_STANDOFF_TOLERANCE;
            if (horizontalDistance > maxDistance) {
                horizontalIntent = horizontalDelta.scale(1.0D / horizontalDistance);
                targetHorizontalSpeed = SUMMON_APPROACH_SPEED;
            } else if (horizontalDistance < minDistance) {
                horizontalIntent = horizontalDelta.scale(-1.0D / horizontalDistance);
                targetHorizontalSpeed = SUMMON_CORRECTION_SPEED;
            }
        }

        targetHorizontalSpeed *= this.getSpeedMultiplier();
        double verticalIntent = Mth.clamp(verticalDelta * SUMMON_VERTICAL_TRACK_FACTOR, -1.0D, 1.0D);

        if (horizontalIntent.lengthSqr() > 1.0E-5D) {
            float previousYaw = this.getYRot();
            float targetYaw = (float) (Mth.atan2(-horizontalIntent.x, horizontalIntent.z) * Mth.RAD_TO_DEG);
            this.setYRot(Mth.rotLerp(0.45F, previousYaw, targetYaw));
            this.yRotO = previousYaw;
        }

        float previousPitch = this.getXRot();
        this.setXRot(Mth.lerp(0.2F, this.getXRot(), (float) (-verticalIntent * SUMMON_PITCH_FACTOR)));
        this.xRotO = previousPitch;
        this.updateSideTilt(0.0F);

        Vec3 desiredVelocity = new Vec3(
                horizontalIntent.x * targetHorizontalSpeed,
                verticalIntent * SUMMON_VERTICAL_SPEED,
                horizontalIntent.z * targetHorizontalSpeed
        );
        Vec3 velocity = this.getDeltaMovement().lerp(desiredVelocity, SUMMON_VELOCITY_LERP);

        if (horizontalIntent.lengthSqr() < 1.0E-5D) {
            velocity = new Vec3(velocity.x * 0.8D, velocity.y, velocity.z * 0.8D);
        }
        if (Math.abs(verticalIntent) < 0.02D) {
            velocity = new Vec3(velocity.x, velocity.y * 0.7D, velocity.z);
        }

        velocity = this.clampVelocity(velocity);
        this.setDeltaMovement(velocity);
    }

    @Nullable
    private Player resolveSummonTargetPlayer() {
        if (this.summonTargetUUID == null) {
            return null;
        }

        for (Player player : this.level().players()) {
            if (this.summonTargetUUID.equals(player.getUUID())) {
                return player;
            }
        }
        return null;
    }

    public void setOwnerUUID(@Nullable UUID ownerUUID) {
        this.ownerUUID = ownerUUID;
    }

    @Nullable
    public UUID getOwnerUUID() {
        return this.ownerUUID;
    }

    public boolean isOwnedBy(Player player) {
        return this.ownerUUID == null || this.ownerUUID.equals(player.getUUID());
    }

    public boolean canBeSummonedBy(Player player) {
        if (!this.isAlive() || !this.isOwnedBy(player)) {
            return false;
        }

        LivingEntity controller = this.getControllingPassenger();
        if (controller != null && controller != player) {
            return false;
        }

        return !this.isVehicle() || this.hasPassenger(player);
    }

    public boolean summonTo(Player player) {
        if (!this.canBeSummonedBy(player)) {
            return false;
        }

        if (this.ownerUUID == null) {
            this.ownerUUID = player.getUUID();
        }
        this.summonTargetUUID = player.getUUID();
        if (!this.hasPassenger(player)) {
            this.setDeltaMovement(this.getDeltaMovement().multiply(0.8D, 0.8D, 0.8D));
        }
        return true;
    }

    private void updateSideTilt(float strafeInput) {
        float targetTilt = Mth.clamp(strafeInput, -1.0F, 1.0F) * MAX_SIDE_TILT;
        this.setSideTilt(Mth.lerp(SIDE_TILT_LERP, this.getSideTilt(), targetTilt));
    }

    public float getSideTilt() {
        return this.entityData.get(DATA_ID_SIDE_TILT);
    }

    private void setSideTilt(float sideTilt) {
        this.entityData.set(DATA_ID_SIDE_TILT, sideTilt);
    }

    private Vec3 clampVelocity(Vec3 velocity) {
        Vec3 horizontal = new Vec3(velocity.x, 0.0D, velocity.z);
        double horizontalSpeed = horizontal.length();
        double maxHorizontalSpeed = MAX_HORIZONTAL_SPEED * this.getSpeedMultiplier();
        if (horizontalSpeed > maxHorizontalSpeed) {
            double scale = maxHorizontalSpeed / horizontalSpeed;
            velocity = new Vec3(velocity.x * scale, velocity.y, velocity.z * scale);
        }
        return new Vec3(velocity.x, Mth.clamp(velocity.y, -MAX_VERTICAL_SPEED, MAX_VERTICAL_SPEED), velocity.z);
    }

    @Override
    public InteractionResult interact(Player player, InteractionHand hand) {
        InteractionResult result = super.interact(player, hand);
        if (result != InteractionResult.PASS) {
            return result;
        }

        if (player.isSecondaryUseActive()) {
            InteractionResult inventoryResult = this.tryOpenInventory(player);
            if (inventoryResult != InteractionResult.PASS) {
                return inventoryResult;
            }
        }

        if (!this.canPlayerRide(player)) {
            return this.level().isClientSide ? InteractionResult.SUCCESS : InteractionResult.CONSUME;
        }

        if (!this.level().isClientSide) {
            return player.startRiding(this) ? InteractionResult.CONSUME : InteractionResult.PASS;
        }
        return InteractionResult.SUCCESS;
    }

    private InteractionResult tryOpenInventory(Player player) {
        int capacity = this.getInventoryCapacity();
        if (capacity <= 0) {
            return InteractionResult.PASS;
        }

        if (!this.isOwnedBy(player)) {
            if (!this.level().isClientSide) {
                player.displayClientMessage(Component.translatable("chat.ars_additions.magic_carpet.inventory_owner_only"), true);
            }
            return InteractionResult.CONSUME;
        }

        if (this.level().isClientSide) {
            return InteractionResult.SUCCESS;
        }

        if (!(player instanceof ServerPlayer serverPlayer)) {
            return InteractionResult.PASS;
        }
        this.openInventory(serverPlayer, capacity);
        return InteractionResult.CONSUME;
    }

    private void openInventory(ServerPlayer player, int capacity) {
        int rows = Mth.clamp(capacity / 9, 1, 3);
        MenuProvider provider = new SimpleMenuProvider(
                (containerId, inventory, entityPlayer) -> {
                    CarpetInventoryContainer container = new CarpetInventoryContainer(this, capacity);
                    return switch (rows) {
                        case 1 -> new ChestMenu(MenuType.GENERIC_9x1, containerId, inventory, container, 1);
                        case 2 -> new ChestMenu(MenuType.GENERIC_9x2, containerId, inventory, container, 2);
                        default -> ChestMenu.threeRows(containerId, inventory, container);
                    };
                },
                Component.translatable("container.ars_additions.magic_carpet_inventory")
        );
        player.openMenu(provider);
    }

    public int getInventoryCapacity() {
        return getInventoryCapacityForStack(this.getCarpetStack());
    }

    private void saveInventoryFromContainer(SimpleContainer container) {
        ItemStack stack = this.getCarpetStack().copy();
        MagicCarpetInventoryData data = MagicCarpetInventoryData.fromStack(stack).orElse(MagicCarpetInventoryData.empty());
        List<ItemStack> items = data.mutableItems();
        for (int i = 0; i < container.getContainerSize(); i++) {
            ItemStack containerStack = container.getItem(i);
            items.set(i, containerStack.isEmpty() ? ItemStack.EMPTY : containerStack.copy());
        }
        stack.set(AddonDataComponentRegistry.MAGIC_CARPET_INVENTORY, new MagicCarpetInventoryData(items));
        this.setCarpetStack(stack);
    }

    @Override
    protected void checkFallDamage(double y, boolean onGround, BlockState state, BlockPos pos) {
        this.resetFallDistance();
    }

    @Override
    public boolean isPickable() {
        return this.isAlive();
    }

    @Override
    public boolean canBeCollidedWith() {
        return this.isAlive();
    }

    @Override
    public boolean canCollideWith(Entity entity) {
        return (entity.canBeCollidedWith() || entity.isPushable()) && !this.isPassengerOfSameVehicle(entity);
    }

    @Override
    public boolean isPushable() {
        return this.isAlive();
    }

    @Override
    public boolean canSprint() {
        return false;
    }

    @Override
    public void push(Entity entity) {
        if (!this.hasPassenger(entity)) {
            if (entity instanceof MagicCarpetEntity) {
                if (entity.getBoundingBox().minY < this.getBoundingBox().maxY) {
                    super.push(entity);
                }
            } else if (entity.getBoundingBox().minY <= this.getBoundingBox().minY) {
                super.push(entity);
            }
        }
    }

    @Override
    protected void removePassenger(Entity passenger) {
        super.removePassenger(passenger);
        if (!(this.getControllingPassenger() instanceof Player)) {
            this.descendHoldTicks = 0;
            this.descendInputActive = false;
        }
        if (!this.isVehicle()) {
            this.setDeltaMovement(Vec3.ZERO);
            this.lerpSteps = 0;
        }
    }

    @Override
    protected boolean canAddPassenger(Entity passenger) {
        return passenger instanceof Player player
                && this.getPassengers().size() < this.getMaxPassengers()
                && this.canPlayerRide(player);
    }

    private int getMaxPassengers() {
        return this.isLargeCarpet() ? LARGE_MAX_PASSENGERS : SMALL_MAX_PASSENGERS;
    }

    private boolean canPlayerRide(Player player) {
        if (this.isOwnedBy(player)) {
            return true;
        }

        return this.getControllingPassenger() instanceof Player controller && this.isOwnedBy(controller);
    }

    @Nullable
    @Override
    public LivingEntity getControllingPassenger() {
        if (this.ownerUUID != null) {
            for (Entity passenger : this.getPassengers()) {
                if (this.ownerUUID.equals(passenger.getUUID()) && passenger instanceof LivingEntity livingEntity) {
                    return livingEntity;
                }
            }
            return null;
        }
        return this.getFirstPassenger() instanceof LivingEntity livingEntity ? livingEntity : null;
    }

    @Override
    protected Vec3 getPassengerAttachmentPoint(Entity passenger, EntityDimensions dimensions, float partialTick) {
        Vec2 seatOffset = this.getPassengerSeatOffset(passenger);
        double yOffset = (double) (dimensions.height() / 3.0F) + PASSENGER_HEIGHT_OFFSET;
        float pitchRadians = Mth.lerp(partialTick, this.xRotO, this.getXRot()) * Mth.DEG_TO_RAD;
        float yawRadians = Mth.lerp(partialTick, this.yRotO, this.getYRot()) * Mth.DEG_TO_RAD;

        double localX = seatOffset.x;
        double localY = yOffset;
        double localZ = seatOffset.y;

        // Rotate seat point with carpet pitch so riders stay attached when leaning forward/back.
        double pitchedY = localY * Mth.cos(pitchRadians) - localZ * Mth.sin(pitchRadians);
        double pitchedZ = localY * Mth.sin(pitchRadians) + localZ * Mth.cos(pitchRadians);

        double worldX = localX * Mth.cos(yawRadians) - pitchedZ * Mth.sin(yawRadians);
        double worldZ = pitchedZ * Mth.cos(yawRadians) + localX * Mth.sin(yawRadians);
        return new Vec3(worldX, pitchedY, worldZ);
    }

    @Override
    protected void positionRider(Entity passenger, MoveFunction callback) {
        super.positionRider(passenger, callback);
        if (passenger instanceof LivingEntity livingEntity) {
            livingEntity.yBodyRot = this.getYRot();
        }
    }

    @Override
    public Vec3 getDismountLocationForPassenger(LivingEntity livingEntity) {
        Vec3 topCenter = new Vec3(this.getX(), this.getBoundingBox().maxY + DISMOUNT_TOP_OFFSET, this.getZ());
        for (Pose pose : livingEntity.getDismountPoses()) {
            if (DismountHelper.canDismountTo(this.level(), topCenter, livingEntity, pose)) {
                livingEntity.setPose(pose);
                this.stabilizeDismount(livingEntity);
                return topCenter;
            }
        }
        this.stabilizeDismount(livingEntity);
        return topCenter;
    }

    private void stabilizeDismount(LivingEntity livingEntity) {
        Vec3 motion = livingEntity.getDeltaMovement();
        livingEntity.setDeltaMovement(motion.x, 0.0D, motion.z);
        livingEntity.resetFallDistance();
    }

    @Override
    public ItemStack getPickResult() {
        return this.getCarpetStack().copyWithCount(1);
    }

    @Override
    protected AABB makeBoundingBox() {
        Direction facing = this.hitboxFacing;
        if (facing == null) {
            facing = Direction.fromYRot(this.getYRot());
            if (facing == null) {
                facing = Direction.NORTH;
            }
        }
        return this.makeBoundingBoxForFacing(facing);
    }

    private AABB makeBoundingBoxForFacing(Direction facing) {
        float yawRadians = this.getYRot() * Mth.DEG_TO_RAD;
        double sinYaw = Mth.sin(yawRadians);
        double cosYaw = Mth.cos(yawRadians);
        boolean large = this.isLargeCarpet();
        double localXOffset = large ? LARGE_HITBOX_LOCAL_X_OFFSET : SMALL_HITBOX_LOCAL_X_OFFSET;
        double localZOffset = large ? LARGE_HITBOX_LOCAL_Z_OFFSET : SMALL_HITBOX_LOCAL_Z_OFFSET;
        double centerX = this.getX() + (localXOffset * cosYaw - localZOffset * sinYaw);
        double centerZ = this.getZ() + (localZOffset * cosYaw + localXOffset * sinYaw);
        double halfWidth = large ? LARGE_HITBOX_HALF_WIDTH : SMALL_HITBOX_HALF_WIDTH;
        double halfLength = large ? LARGE_HITBOX_HALF_LENGTH : SMALL_HITBOX_HALF_LENGTH;
        double halfX = facing.getAxis() == Direction.Axis.X ? halfLength : halfWidth;
        double halfZ = facing.getAxis() == Direction.Axis.X ? halfWidth : halfLength;
        double minY = this.getY();
        return new AABB(centerX - halfX, minY, centerZ - halfZ, centerX + halfX, minY + HITBOX_HEIGHT, centerZ + halfZ);
    }

    private void updateHitboxFacingSafely() {
        Direction desiredFacing = Direction.fromYRot(this.getYRot());
        Direction currentFacing = this.hitboxFacing == null ? Direction.NORTH : this.hitboxFacing;
        if (desiredFacing == currentFacing) {
            // Keep center offset aligned to current yaw even when cardinal facing is unchanged.
            this.setBoundingBox(this.makeBoundingBoxForFacing(currentFacing));
            return;
        }

        AABB targetBox = this.makeBoundingBoxForFacing(desiredFacing);
        AABB clearanceBox = targetBox.inflate(HITBOX_ROTATION_CLEARANCE, 0.0D, HITBOX_ROTATION_CLEARANCE);
        if (this.level().noBlockCollision(this, clearanceBox)) {
            this.hitboxFacing = desiredFacing;
            this.setBoundingBox(targetBox);
        } else {
            // Fallback keeps center in sync if we cannot rotate to the next axis-aligned hitbox.
            this.setBoundingBox(this.makeBoundingBoxForFacing(currentFacing));
        }
    }

    @Override
    protected Item getDropItem() {
        return AddonItemRegistry.MAGIC_CARPET.get();
    }

    @Override
    protected void destroy(DamageSource damageSource) {
        this.kill();
        if (this.level().getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS)) {
            ItemStack stack = this.getCarpetStack().copyWithCount(1);
            stack.set(DataComponents.CUSTOM_NAME, this.getCustomName());
            this.spawnAtLocation(stack);
        }
    }

    public ItemStack getCarpetStack() {
        return this.entityData.get(DATA_ID_CARPET_STACK);
    }

    public void setCarpetStack(ItemStack stack) {
        ItemStack normalized = normalizeCarpetStack(stack);
        ItemStack previous = this.getCarpetStack();
        boolean wasLarge = isLargeCarpetForStack(previous);
        boolean isLarge = isLargeCarpetForStack(normalized);
        if (!this.level().isClientSide && !previous.isEmpty()) {
            spillOverflowAfterCapacityDrop(normalized, previous, this.level(), this.position());
        }
        this.entityData.set(DATA_ID_CARPET_STACK, normalized);
        if (wasLarge != isLarge) {
            Direction facing = this.hitboxFacing == null ? Direction.NORTH : this.hitboxFacing;
            this.setBoundingBox(this.makeBoundingBoxForFacing(facing));
        }
    }

    public boolean isLargeCarpet() {
        return isLargeCarpetForStack(this.getCarpetStack());
    }

    public double getModelHalfLength() {
        return this.isLargeCarpet() ? LARGE_MODEL_HALF_LENGTH : SMALL_MODEL_HALF_LENGTH;
    }

    public double getModelHalfWidth() {
        return this.isLargeCarpet() ? LARGE_MODEL_HALF_WIDTH : SMALL_MODEL_HALF_WIDTH;
    }

    public float getCarpetShadowRadius() {
        return this.isLargeCarpet() ? LARGE_SHADOW_RADIUS : SMALL_SHADOW_RADIUS;
    }

    private double getSpeedMultiplier() {
        int slotValue = getSlotValueForPerk(this.getCarpetStack(), CarpetSpeedPerk.INSTANCE);
        return 1.0D + (slotValue * SPEED_THREAD_BONUS_PER_SLOT);
    }

    public static boolean isLargeCarpetForStack(ItemStack stack) {
        return getSlotValueForPerk(stack, CarpetSizePerk.INSTANCE) > 0;
    }

    public static int getInventoryCapacityForStack(ItemStack stack) {
        int slotTier = Mth.clamp(getEffectiveSlotTierForPerk(stack, CarpetInventoryPerk.INSTANCE), 0, 3);
        return slotTier * 9;
    }

    private static int getEffectiveSlotTierForPerk(ItemStack stack, IPerk targetPerk) {
        ArmorPerkHolder holder = PerkUtil.getPerkHolder(stack);
        if (holder == null) {
            return 0;
        }

        List<IPerk> perks = holder.getPerks();
        List<PerkSlot> activeSlots = holder.getSlotsForTier(stack);
        int pairedSize = Math.min(perks.size(), activeSlots.size());
        for (int i = 0; i < pairedSize; i++) {
            if (perks.get(i).equals(targetPerk)) {
                return activeSlots.get(i).value();
            }
        }
        return 0;
    }

    private static int getSlotValueForPerk(ItemStack stack, IPerk targetPerk) {
        ArmorPerkHolder holder = PerkUtil.getPerkHolder(stack);
        if (holder == null) {
            return 0;
        }

        int slotValue = 0;
        for (PerkInstance instance : holder.getPerkInstances(stack)) {
            if (instance.getPerk().equals(targetPerk)) {
                slotValue = Math.max(slotValue, instance.getSlot().value());
            }
        }
        return slotValue;
    }

    public static void spillOverflowAfterCapacityDrop(ItemStack updatedStack, ItemStack previousStack, Level level, Vec3 dropPosition) {
        if (level.isClientSide) {
            return;
        }

        int oldCapacity = getInventoryCapacityForStack(previousStack);
        int newCapacity = getInventoryCapacityForStack(updatedStack);
        if (newCapacity >= MagicCarpetInventoryData.SLOT_COUNT) {
            return;
        }
        if (oldCapacity > 0 && newCapacity >= oldCapacity) {
            return;
        }

        MagicCarpetInventoryData data = updatedStack.get(AddonDataComponentRegistry.MAGIC_CARPET_INVENTORY);
        if (data == null) {
            return;
        }

        List<ItemStack> items = data.mutableItems();
        boolean spilledAny = false;
        for (int i = Math.max(newCapacity, 0); i < MagicCarpetInventoryData.SLOT_COUNT; i++) {
            ItemStack stack = items.get(i);
            if (stack.isEmpty()) {
                continue;
            }
            level.addFreshEntity(new ItemEntity(level, dropPosition.x, dropPosition.y + 0.25D, dropPosition.z, stack.copy()));
            items.set(i, ItemStack.EMPTY);
            spilledAny = true;
        }

        if (spilledAny) {
            updatedStack.set(AddonDataComponentRegistry.MAGIC_CARPET_INVENTORY, new MagicCarpetInventoryData(items));
        }
    }

    private static ItemStack createBaseCarpetStack() {
        ItemStack base = new ItemStack(AddonItemRegistry.MAGIC_CARPET.get());
        if (!base.has(DataComponentRegistry.ARMOR_PERKS)) {
            base.set(DataComponentRegistry.ARMOR_PERKS, new ArmorPerkHolder());
        }
        if (!base.has(AddonDataComponentRegistry.MAGIC_CARPET_INVENTORY)) {
            base.set(AddonDataComponentRegistry.MAGIC_CARPET_INVENTORY, MagicCarpetInventoryData.empty());
        }
        return base;
    }

    private static ItemStack normalizeCarpetStack(ItemStack stack) {
        ItemStack normalized;
        if (stack.isEmpty() || !stack.is(AddonItemRegistry.MAGIC_CARPET.get())) {
            normalized = createBaseCarpetStack();
        } else {
            normalized = stack.copyWithCount(1);
            if (!normalized.has(DataComponentRegistry.ARMOR_PERKS)) {
                normalized.set(DataComponentRegistry.ARMOR_PERKS, new ArmorPerkHolder());
            }
            if (!normalized.has(AddonDataComponentRegistry.MAGIC_CARPET_INVENTORY)) {
                normalized.set(AddonDataComponentRegistry.MAGIC_CARPET_INVENTORY, MagicCarpetInventoryData.empty());
            }
        }
        return normalized;
    }

    private Vec3 toWorldIntent(Vec3 localIntent, float yawDegrees) {
        if (localIntent.lengthSqr() < 1.0E-7D) {
            return Vec3.ZERO;
        }

        float yawRadians = yawDegrees * Mth.DEG_TO_RAD;
        float sinYaw = Mth.sin(yawRadians);
        float cosYaw = Mth.cos(yawRadians);
        return new Vec3(
                localIntent.x * cosYaw - localIntent.z * sinYaw,
                0.0D,
                localIntent.z * cosYaw + localIntent.x * sinYaw
        );
    }

    private Vec3 getRiddenInput(Player rider) {
        float strafe = rider.xxa * 0.5F;
        float forward = rider.zza;
        if (forward <= 0.0F) {
            forward *= 0.25F;
        }
        return new Vec3(strafe, 0.0D, forward);
    }

    private Vec2 getPassengerSeatOffset(Entity passenger) {
        List<Entity> passengers = this.getPassengers();
        int index = passengers.indexOf(passenger);
        if (index < 0) {
            return Vec2.ZERO;
        }

        if (!this.isLargeCarpet()) {
            if (passengers.size() <= 1) {
                return Vec2.ZERO;
            }
            return index == 0 ? new Vec2(0.0F, SMALL_FRONT_PASSENGER_Z) : new Vec2(0.0F, SMALL_BACK_PASSENGER_Z);
        }

        Entity driver = this.getControllingPassenger();
        if (driver == null && !passengers.isEmpty()) {
            driver = passengers.get(0);
        }

        if (passenger == driver) {
            return new Vec2(LARGE_DRIVER_SEAT_X, LARGE_DRIVER_SEAT_Z);
        }

        int passengerRank = 0;
        for (Entity rider : passengers) {
            if (rider == driver) {
                continue;
            }
            if (rider == passenger) {
                break;
            }
            passengerRank++;
        }

        return switch (passengerRank) {
            case 0 -> new Vec2(-LARGE_PASSENGER_X_SPREAD, LARGE_PASSENGER_FRONT_ROW_Z);
            case 1 -> new Vec2(LARGE_PASSENGER_X_SPREAD, LARGE_PASSENGER_FRONT_ROW_Z);
            case 2 -> new Vec2(-LARGE_PASSENGER_X_SPREAD, LARGE_PASSENGER_BACK_ROW_Z);
            default -> new Vec2(LARGE_PASSENGER_X_SPREAD, LARGE_PASSENGER_BACK_ROW_Z);
        };
    }

    private PlayState animationPredicate(AnimationState<MagicCarpetEntity> state) {
        Vec3 velocity = this.getDeltaMovement();
        double dx = this.getX() - this.xo;
        double dy = this.getY() - this.yo;
        double dz = this.getZ() - this.zo;
        double horizontalMotion = Math.max(velocity.horizontalDistanceSqr(), dx * dx + dz * dz);
        double verticalMotion = Math.max(Math.abs(velocity.y), Math.abs(dy));
        if (horizontalMotion > 2.5E-3D || verticalMotion > 0.015D) {
            return state.setAndContinue(MOVE_ANIMATION);
        }
        return state.setAndContinue(IDLE_ANIMATION);
    }

    private static class CarpetInventoryContainer extends SimpleContainer {
        private final MagicCarpetEntity carpet;

        public CarpetInventoryContainer(MagicCarpetEntity carpet, int capacity) {
            super(capacity);
            this.carpet = carpet;

            List<ItemStack> stored = MagicCarpetInventoryData.fromStack(carpet.getCarpetStack())
                    .orElse(MagicCarpetInventoryData.empty())
                    .items();
            for (int i = 0; i < capacity; i++) {
                this.getItems().set(i, stored.get(i).copy());
            }
        }

        @Override
        public void setChanged() {
            super.setChanged();
            if (!this.carpet.level().isClientSide) {
                this.carpet.saveInventoryFromContainer(this);
            }
        }

        @Override
        public boolean stillValid(Player player) {
            return this.carpet.isAlive()
                    && this.carpet.isOwnedBy(player)
                    && this.carpet.distanceToSqr(player) <= INVENTORY_INTERACTION_DISTANCE_SQR
                    && this.carpet.getInventoryCapacity() >= this.getContainerSize();
        }
    }
}
