package mrjake.aunis.gui.container;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import mrjake.aunis.Aunis;
import mrjake.aunis.gui.element.Tab;
import mrjake.aunis.gui.element.TabAddress;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;

public class StargateContainerGui extends GuiContainer {
	
	private static final ResourceLocation BACKGROUND_TEXTURE = new ResourceLocation(Aunis.ModID, "textures/gui/container_stargate.png");
	
	private StargateContainer container;
	private List<Tab> tabs;
	
	public StargateContainerGui(StargateContainer container) {
		super(container);
		this.container = container;
		
		this.xSize = 176;
		this.ySize = 168;
	}
	
	@Override
	public void initGui() {
		super.initGui();
		
		tabs = new ArrayList<Tab>();
				
		tabs.add(TabAddress.builder()
				.setGateTile(container.gateTile)
				.setGuiPosition(guiLeft, guiTop)
				.setTabPosition(-21, 2)
				.setOpenPosition(-128)
				.setTabSize(128, 84)
				.setTabTitle(I18n.format("gui.stargate.milky_way_address"))
				.setTexture(BACKGROUND_TEXTURE, 512)
				.setBackgroundTextureLocation(176, 0)
				.setIconRenderPos(1, 7)
				.setIconSize(20, 18)
				.setIconTextureLocation(128, 0).build());
		
		tabs.add(TabAddress.builder()
				.setGateTile(container.gateTile)
				.setGuiPosition(guiLeft, guiTop)
				.setTabPosition(-21, 2+22)
				.setOpenPosition(-128)
				.setTabSize(128, 84)
				.setTabTitle(I18n.format("gui.stargate.milky_way_address"))
				.setTexture(BACKGROUND_TEXTURE, 512)
				.setBackgroundTextureLocation(176, 0)
				.setIconRenderPos(1, 7)
				.setIconSize(20, 18)
				.setIconTextureLocation(128, 18).build());
		
		tabs.add(TabAddress.builder()
				.setGateTile(container.gateTile)
				.setGuiPosition(guiLeft, guiTop)
				.setTabPosition(-21, 2+22*2)
				.setOpenPosition(-128)
				.setTabSize(128, 84)
				.setTabTitle(I18n.format("gui.stargate.milky_way_address"))
				.setTexture(BACKGROUND_TEXTURE, 512)
				.setBackgroundTextureLocation(176, 0)
				.setIconRenderPos(1, 7)
				.setIconSize(20, 18)
				.setIconTextureLocation(128, 18*2).build());
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		drawDefaultBackground();
				
		super.drawScreen(mouseX, mouseY, partialTicks);
		renderHoveredToolTip(mouseX, mouseY);
	}
	
	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {		
		for (Tab tab : tabs) {
			tab.render(fontRenderer);
		}
		
		mc.getTextureManager().bindTexture(BACKGROUND_TEXTURE);
		GlStateManager.color(1,1,1, 1);
		drawModalRectWithCustomSizedTexture(guiLeft, guiTop, 0, 0, xSize, ySize, 512, 512);
		
		drawGradientRect(guiLeft+10, guiTop+61, guiLeft+10+156/4/2, guiTop+61+6, 0xffcc2828, 0xff731616);
	}
	
	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		fontRenderer.drawString(I18n.format("gui.stargate.capacitors"), 112, 29, 4210752);
		
		String energy = "23.54 %";
		fontRenderer.drawString(energy, 168-fontRenderer.getStringWidth(energy)+2, 71, 4210752);

		fontRenderer.drawString(I18n.format("gui.upgrades"), 7, 6, 4210752);
        fontRenderer.drawString(I18n.format("container.inventory"), 8, this.ySize - 96 + 2, 4210752);
		
		for (Tab tab : tabs) {
			tab.renderFg(this, fontRenderer, mouseX, mouseY);
		}
	}

	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
		super.mouseClicked(mouseX, mouseY, mouseButton);
		
		for (int i=0; i<tabs.size(); i++) {
			Tab tab = tabs.get(i);
			
			if (tab.isCursorOnTab(mouseX, mouseY)) {
				Tab.tabsInteract(tabs, i);
				
				break;
			}
		}
	}
}
