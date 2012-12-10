package icbm;

import icbm.jiqi.TCiGuiPao;
import icbm.jiqi.TDianCiQi;
import icbm.jiqi.TFaSheDi;
import icbm.jiqi.TFaSheJia;
import icbm.jiqi.TFaSheShiMuo;
import icbm.jiqi.TLeiDaTai;
import icbm.jiqi.TXiaoFaSheQi;
import icbm.jiqi.TYinGanQi;
import icbm.rongqi.CCiGuiPao;
import icbm.rongqi.CFaShiDi;
import icbm.rongqi.CXiaoFaSheQi;
import icbm.zhapin.TZhaDan;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.TileEntity;
import net.minecraft.src.World;
import universalelectricity.prefab.multiblock.TileEntityMulti;
import cpw.mods.fml.common.network.IGuiHandler;
import cpw.mods.fml.common.registry.GameRegistry;

public class ICBMCommon implements IGuiHandler
{
	// GUI IDs
	public static final int GUI_RAIL_GUN = 0;
	public static final int GUI_CRUISE_LAUNCHER = 1;
	public static final int GUI_LAUNCHER_SCREEN = 2;
	public static final int GUI_RADAR_STATION = 3;
	public static final int GUI_DETECTOR = 4;
	public static final int GUI_FREQUENCY = 5;
	public static final int GUI_EMP_TOWER = 6;
	public static final int GUI_LAUNCHER_BASE = 7;
	public static final int GUI_LASER_TURRET = 8;

	public void preInit()
	{
	}

	public void init()
	{
		GameRegistry.registerTileEntity(TCiGuiPao.class, "ICBMCiGuiPao");
		GameRegistry.registerTileEntity(TXiaoFaSheQi.class, "ICBMXiaoFaSheQi");
		GameRegistry.registerTileEntity(TFaSheDi.class, "ICBMFaSheDi");
		GameRegistry.registerTileEntity(TFaSheShiMuo.class, "ICBMFaSheShiMuo");
		GameRegistry.registerTileEntity(TFaSheJia.class, "ICBMFaSheJia");
		GameRegistry.registerTileEntity(TLeiDaTai.class, "ICBMLeiDaTai");
		GameRegistry.registerTileEntity(TDianCiQi.class, "ICBMDianCiQi");
		GameRegistry.registerTileEntity(TYinXing.class, "ICBMYinXin");

		GameRegistry.registerTileEntity(TZhaDan.class, "ICBMZhaDan");
		GameRegistry.registerTileEntity(TYinGanQi.class, "ICBMYinGanQi");
		GameRegistry.registerTileEntity(TileEntityMulti.class, "ICBMMulti");
	}

	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
	{
		return null;
	}

	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
	{
		TileEntity tileEntity = world.getBlockTileEntity(x, y, z);

		if (tileEntity != null)
		{
			switch (ID)
			{
				case ICBMCommon.GUI_RAIL_GUN:
					return new CCiGuiPao(player.inventory, (TCiGuiPao) tileEntity);
				case ICBMCommon.GUI_CRUISE_LAUNCHER:
					return new CXiaoFaSheQi(player.inventory, (TXiaoFaSheQi) tileEntity);
				case ICBMCommon.GUI_LAUNCHER_BASE:
					return new CFaShiDi(player.inventory, (TFaSheDi) tileEntity);
			}
		}

		return null;
	}

	public String getMinecraftDir()
	{
		return "";
	}
	
	public boolean isGaoQing()
	{
		return false;
	}
}
