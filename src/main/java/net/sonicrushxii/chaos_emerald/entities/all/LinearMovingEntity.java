package net.sonicrushxii.chaos_emerald.entities.all;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class LinearMovingEntity extends PointEntity {
    public Vec3 getMovementDirection() {
        return movementDirection;
    }

    public void setMovementDirection(Vec3 movementDirection) {
        this.movementDirection = movementDirection;
    }

    protected Vec3 movementDirection;

    public LinearMovingEntity(EntityType<? extends PointEntity> type, Level world) {
        super(type, world);
        movementDirection = new Vec3(0.0,0.0,0.0);
    }

    @Override
    public void tick() {
        super.tick();
        this.setDeltaMovement(movementDirection);
    }
}
