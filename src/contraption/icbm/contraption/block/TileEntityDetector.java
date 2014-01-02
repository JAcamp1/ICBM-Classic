package icbm.contraption.block;

import icbm.contraption.ICBMContraption;
import icbm.contraption.ItemSignalDisrupter;
import icbm.core.base.TileEnityBase;
import icbm.core.implement.IRedstoneProvider;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.packet.Packet;
import net.minecraft.util.AxisAlignedBB;
import net.minecraftforge.common.ForgeDirection;
import universalelectricity.api.vector.Vector3;

import com.builtbroken.minecraft.network.PacketHandler;
import com.builtbroken.minecraft.prefab.invgui.ContainerFake;
import com.google.common.io.ByteArrayDataInput;

import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.common.network.Player;

public class TileEntityDetector extends TileEnityBase implements IRedstoneProvider
{
    private static final int MAX_DISTANCE = 30;

    private static final float DIAN = 5;

    public short frequency = 0;

    public boolean isDetect = false;

    public Vector3 minCoord = new Vector3(9, 9, 9);
    public Vector3 maxCoord = new Vector3(9, 9, 9);

    public byte mode = 0;

    private final Set<EntityPlayer> yongZhe = new HashSet<EntityPlayer>();

    public boolean isInverted = false;

    public TileEntityDetector()
    {
        super(5);
        this.hasGUI = true;
    }

    @Override
    public Class<? extends Container> getContainer()
    {
        return ContainerFake.class;
    }

    @Override
    public void initiate()
    {
        super.initiate();
        this.worldObj.notifyBlocksOfNeighborChange(this.xCoord, this.yCoord, this.zCoord, this.getBlockType().blockID);
    }

    @Override
    public void updateEntity()
    {
        super.updateEntity();

        if (!this.worldObj.isRemote)
        {
            if (this.ticks % 20 == 0)
            {
                for (EntityPlayer wanJia : this.yongZhe)
                {
                    PacketDispatcher.sendPacketToPlayer(this.getDescriptionPacket(), (Player) wanJia);
                }

                boolean isDetectThisCheck = false;

                if (this.isFunctioning())
                {
                    AxisAlignedBB bounds = AxisAlignedBB.getBoundingBox(this.xCoord - minCoord.x, this.yCoord - minCoord.y, this.zCoord - minCoord.z, this.xCoord + maxCoord.x + 1D, this.yCoord + maxCoord.y + 1D, this.zCoord + maxCoord.z + 1D);
                    List<EntityLivingBase> entitiesNearby = worldObj.getEntitiesWithinAABB(EntityLivingBase.class, bounds);

                    for (EntityLivingBase entity : entitiesNearby)
                    {
                        if (entity instanceof EntityPlayer && (this.mode == 0 || this.mode == 1))
                        {
                            boolean gotDisrupter = false;

                            for (ItemStack inventory : ((EntityPlayer) entity).inventory.mainInventory)
                            {
                                if (inventory != null)
                                {
                                    if (inventory.getItem() instanceof ItemSignalDisrupter)
                                    {
                                        if (((ItemSignalDisrupter) inventory.getItem()).getFrequency(inventory) == this.frequency)
                                        {
                                            gotDisrupter = true;
                                            break;
                                        }
                                    }
                                }
                            }

                            if (gotDisrupter)
                            {
                                if (this.isInverted)
                                {
                                    isDetectThisCheck = true;
                                    break;
                                }

                                continue;
                            }

                            if (!this.isInverted)
                            {
                                isDetectThisCheck = true;
                            }
                        }
                        else if (!this.isInverted && !(entity instanceof EntityPlayer) && (this.mode == 0 || this.mode == 2))
                        {
                            isDetectThisCheck = true;
                            break;
                        }
                    }
                }

                if (isDetectThisCheck != this.isDetect)
                {
                    this.isDetect = isDetectThisCheck;
                    this.worldObj.notifyBlocksOfNeighborChange(this.xCoord, this.yCoord, this.zCoord, this.getBlockType().blockID);
                }

            }
        }
    }

    @Override
    public Packet getDescriptionPacket()
    {
        return PacketHandler.instance().getTilePacket(ICBMContraption.CHANNEL, "desc", this, this.getEnergyStored(), this.frequency, this.mode, this.isInverted, this.minCoord.intX(), this.minCoord.intY(), this.minCoord.intZ(), this.maxCoord.intX(), this.maxCoord.intY(), this.maxCoord.intZ());
    }

    @Override
    public boolean simplePacket(String id, ByteArrayDataInput data, Player player)
    {
        try
        {
            if (!super.simplePacket(id, data, player))
            {
                if (id.equalsIgnoreCase("desc"))
                {
                    this.setEnergy(ForgeDirection.UNKNOWN, data.readLong());
                    this.frequency = data.readShort();
                    this.mode = data.readByte();
                    this.isInverted = data.readBoolean();
                    this.minCoord = new Vector3(Math.max(0, Math.min(MAX_DISTANCE, data.readInt())), Math.max(0, Math.min(MAX_DISTANCE, data.readInt())), Math.max(0, Math.min(MAX_DISTANCE, data.readInt())));
                    this.maxCoord = new Vector3(Math.max(0, Math.min(MAX_DISTANCE, data.readInt())), Math.max(0, Math.min(MAX_DISTANCE, data.readInt())), Math.max(0, Math.min(MAX_DISTANCE, data.readInt())));
                    return true;
                }
                else if (id.equalsIgnoreCase("mode"))
                {
                    this.mode = data.readByte();
                    return true;
                }
                else if (id.equalsIgnoreCase("freq"))
                {
                    this.frequency = data.readShort();
                    return true;
                }
                else if (id.equalsIgnoreCase("minVec"))
                {
                    this.minCoord = new Vector3(Math.max(0, Math.min(MAX_DISTANCE, data.readInt())), Math.max(0, Math.min(MAX_DISTANCE, data.readInt())), Math.max(0, Math.min(MAX_DISTANCE, data.readInt())));
                    return true;
                }
                else if (id.equalsIgnoreCase("maxVec"))
                {
                    this.maxCoord = new Vector3(Math.max(0, Math.min(MAX_DISTANCE, data.readInt())), Math.max(0, Math.min(MAX_DISTANCE, data.readInt())), Math.max(0, Math.min(MAX_DISTANCE, data.readInt())));
                    return true;
                }
            }
            else
            {
                return true;
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return true;
        }
        return false;
    }

    /** Reads a tile entity from NBT. */
    @Override
    public void readFromNBT(NBTTagCompound nbt)
    {
        super.readFromNBT(nbt);

        this.mode = nbt.getByte("mode");
        this.frequency = nbt.getShort("frequency");
        this.isInverted = nbt.getBoolean("isInverted");

        this.minCoord = new Vector3(nbt.getCompoundTag("minCoord"));
        this.maxCoord = new Vector3(nbt.getCompoundTag("maxCoord"));
    }

    /** Writes a tile entity to NBT. */
    @Override
    public void writeToNBT(NBTTagCompound nbt)
    {
        super.writeToNBT(nbt);

        nbt.setShort("frequency", this.frequency);
        nbt.setByte("mode", this.mode);
        nbt.setBoolean("isInverted", this.isInverted);

        nbt.setCompoundTag("minCoord", this.minCoord.writeToNBT(new NBTTagCompound()));
        nbt.setCompoundTag("maxCoord", this.maxCoord.writeToNBT(new NBTTagCompound()));
    }

    @Override
    public boolean isPoweringTo(ForgeDirection side)
    {
        return this.isDetect;
    }

    @Override
    public boolean isIndirectlyPoweringTo(ForgeDirection side)
    {
        return this.isDetect;
    }
}