package io.sc3.peripherals.client.gui

import com.mojang.blaze3d.systems.RenderSystem
import io.sc3.peripherals.ScPeripherals.ModId
import io.sc3.peripherals.ScPeripherals.modId
import io.sc3.peripherals.posters.printer.PosterPrinterScreenHandler
import io.sc3.peripherals.prints.printer.PrinterBlockEntity.Companion.maxInk
import net.minecraft.client.gui.screen.ingame.HandledScreen
import net.minecraft.client.render.GameRenderer
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.text.Text

class PosterPrinterScreen(
  handler: PosterPrinterScreenHandler,
  playerInv: PlayerInventory,
  title: Text
) : HandledScreen<PosterPrinterScreenHandler>(handler, playerInv, title) {
  private lateinit var inkBar: ProgressBar
  private lateinit var bars: List<ProgressBar>

  override fun init() {
    super.init()

    inkBar = addDrawable(ProgressBar(tex, x + 19, y + 17, inkU, inkV, width = 12, height = 30,
      max = maxInk, tooltipKey = "gui.$modId.printer.ink", direction = ProgressBar.Direction.BOTTOM_TO_TOP))
    bars = listOf(inkBar)
  }

  override fun drawBackground(matrices: MatrixStack, delta: Float, mouseX: Int, mouseY: Int) {
    RenderSystem.setShader(GameRenderer::getPositionTexProgram)
    RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f)
    RenderSystem.setShaderTexture(0, tex)

    // Background
    drawTexture(matrices, x, y, 0, 0, backgroundWidth, backgroundHeight)

    // Print progress
    val maxProgress = handler.maxPrintProgress.toDouble().coerceAtLeast(1.0) // Avoid div0 if the server sets this to 0
    val progress = handler.printProgress.toDouble() / maxProgress * progressWidth
    drawTexture(matrices, x + 83, y + 36, 0, 166, progress.toInt(), 14)
  }

  override fun render(matrices: MatrixStack, mouseX: Int, mouseY: Int, delta: Float) {
    renderBackground(matrices)

    inkBar.progress = handler.be?.ink ?: 0

    super.render(matrices, mouseX, mouseY, delta)
    drawMouseoverTooltip(matrices, mouseX, mouseY)

    bars.forEach { bar -> bar.tooltip(mouseX, mouseY)?.let {
      renderTooltip(matrices, it, mouseX, mouseY )}
    }
  }

  companion object {
    val tex = ModId("textures/gui/container/poster_printer.png")

    const val inkU = 177
    const val inkV = 1
    const val progressWidth = 24
  }
}
