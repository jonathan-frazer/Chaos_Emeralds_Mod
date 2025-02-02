package net.sonicrushxii.chaos_emerald.network.red;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;
import net.sonicrushxii.chaos_emerald.event_handler.client_specific.ClientPacketHandler;
import org.joml.Vector3f;

import java.util.function.Supplier;

public class SuperInfernoParticleS2C
{
    private final double absX, absY, absZ;
    private final short offset;

    public SuperInfernoParticleS2C(double absX, double absY, double absZ, short offset) {
        this.absX = absX;
        this.absY = absY;
        this.absZ = absZ;
        this.offset = offset;
    }

    public SuperInfernoParticleS2C(FriendlyByteBuf buf) {
        this.absX = buf.readDouble();
        this.absY = buf.readDouble();
        this.absZ = buf.readDouble();
        this.offset = buf.readShort();
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeDouble(this.absX);
        buf.writeDouble(this.absY);
        buf.writeDouble(this.absZ);
        buf.writeShort(this.offset);
    }

    public static Vector3f colorSelect(int t)
    {
        return switch (t % 3) {
            case 0 -> new Vector3f(1.0f, 0.0f, 0.0f);
            case 1 -> new Vector3f(1.0f, 1.0f, 0.0f);
            default -> new Vector3f(1.0f, 0.647f, 0.0f);
        };
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            // This code is run on the client side
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
                ClientPacketHandler.superInfernoParticle(this.absX, this.absY, this.absZ, this.offset);
            });
        });

        ctx.get().setPacketHandled(true);
    }
}
