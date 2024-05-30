package com.roh.cropimage

import android.graphics.Bitmap
import android.graphics.PointF
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.unit.IntSize
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.sqrt

class CropUtil constructor(bitmap: Bitmap) {

    private var mBitmap: Bitmap = bitmap

    public var cropType: CropType = CropType.FREE_STYLE


    /**
     * The canvas size of the crop view.
     */
    public var canvasSize: CanvasSize by mutableStateOf(CanvasSize())


    /**
     * The internal rectangle (iRect) representing the region to be cropped.
     */
    public var iRect: IRect by mutableStateOf(IRect())

    /**
     * The touch rectangle region used detecting outside iRect to be moved while dragged touched inside this rect.
     */
    private var touchRect: IRect by mutableStateOf(IRect())

    /**
     * The state indicating whether the touch input is inside the touch rectangle for moving the rectangle.
     */
    private var isTouchedInsideRectMove: Boolean by mutableStateOf(false)

    /**
     * The edge of the rectangle touched to drag and resize it.
     */
    var rectEdgeTouched: RectEdge by mutableStateOf(RectEdge.NULL)

    /**
     * The top-left offset of the rectangle (iRect).
     */
    private var irectTopleft: Offset by mutableStateOf(Offset(0.0f, 0.0f))

    /**
     * The top-left offset of the touch rectangle.
     */
    private var touchAreaRectTopLeft: Offset by mutableStateOf(Offset(0.0f, 0.0f))

    /**
     * The padding inside the internal rectangle for the touch rectangle.
     */
    private val paddingForTouchRect = 70F

    /**
     * The minimum limit for various calculations based on the touch rectangle padding.
     */
    private val minLimit: Float = paddingForTouchRect * 3F

    private var maxSquareLimit: Float = 0F
        set(value) {
            minSquareLimit = value * 0.2F
            field = value
        }

    private var minSquareLimit: Float = maxSquareLimit * 0.3F

    /**
     * The last point updated during drag operations.
     */
    private var lastPointUpdated: Offset? = null

    /**
     * Initializes the crop view by resetting the iRect rectangle.
     */
    init {
        resetCropIRect()
    }


    fun cropImage(mWidth: Int, mHeight: Int): Bitmap {

        val rect = getRectFromPoints()

        val bitmap: Bitmap = Bitmap.createScaledBitmap(mBitmap, mWidth, mHeight, true)

        var imgLef = if (rect.left.toInt() < 0) 0 else rect.left.toInt()
        var imgTop = if (rect.top.toInt() < 0) 0 else rect.top.toInt()

        val imgWidth = if (rect.width.toInt() > mWidth) mWidth else rect.width.toInt()
        val imgHeight =
            if (rect.height.toInt() > mHeight) mHeight else rect.height.toInt()

        if (imgLef + imgWidth > mWidth) {
            imgLef = 0
        }
        if (imgTop + imgHeight > mHeight) {
            imgTop = abs(mHeight - imgHeight)
        }

        val cropBitmap = if (imgWidth <= 0 || imgHeight <= 0){
            Bitmap.createBitmap(
                bitmap,
                0,
                0,
                mWidth,
                mHeight
            )
        } else {
            Bitmap.createBitmap(
                bitmap,
                imgLef,
                imgTop,
                imgWidth,
                imgHeight
            )
        }

        return Bitmap.createScaledBitmap(cropBitmap, mWidth, mHeight, true)

    }

    /**
     *  - Handles the event when the canvas size is changed.
     *      Updates the internal canvasSize property with the new dimensions and
     *      resets the crop rectangle (iRect) accordingly.
     *
     *  @param intSize The new size of the canvas as an [IntSize] object.
     */
    fun onCanvasSizeChanged(intSize: IntSize) {
        canvasSize = CanvasSize(intSize.width.toFloat(), intSize.height.toFloat())
        resetCropIRect()
    }

    fun resetCropIRect() {
        // Irect resetting
        val canWidth = canvasSize.canvasWidth
        val canHeight = canvasSize.canvasHeight

        // Free style Rect positioning
        irectTopleft = Offset(x = 0.0F, y = 0.0F)
        iRect = IRect(topLeft = irectTopleft, size = Size(canWidth, canHeight))

        updateTouchRect()
    }

    fun updateOldRectSize(mIRect: IRect) {
        irectTopleft = mIRect.topLeft
        iRect = mIRect
        updateTouchRect()
    }

    private fun updateTouchRect() {
        // Touch rect resetting
        val size = iRect.size
        val insidePadding = (paddingForTouchRect * 2)
        val touchRectTopleft = Offset(
            x = (irectTopleft.x + paddingForTouchRect),
            y = (irectTopleft.y + paddingForTouchRect)
        )
        touchRect = IRect(
            topLeft = touchRectTopleft,
            size = Size(
                width = (size.width - (insidePadding)),
                height = (size.height - (insidePadding))
            )
        )
    }

    /**
     *  - Handles the initial touch event when starting a drag operation.
     *      Determines if the touch input is inside the touch rectangle, identifies the touched
     *      edge of the rectangle, and updates the last touch point for tracking movement.
     *
     *  @param touchPoint The coordinates of the touch event as an [Offset].
     */
    public fun onDragStart(touchPoint: Offset) { // First event of pointer input
        isTouchedInsideRectMove = isTouchInputInsideTheTouchRect(touchPoint)
        rectEdgeTouched = getRectEdge(touchPoint)
        lastPointUpdated = touchPoint
    }

    public fun onDragEnd() { // Third event of pointer input
        isTouchedInsideRectMove = false
        lastPointUpdated = null
        rectEdgeTouched = RectEdge.NULL
    }

    public fun onDrag(dragPoint: Offset) { // Second event of pointer input
        if (isTouchedInsideRectMove) {
            processIRectDrag(dragPoint = dragPoint)
        } else {
            when (rectEdgeTouched) {
                RectEdge.TOP_LEFT -> {
                    topLeftCornerDrag(dragPoint)
                }

                RectEdge.TOP_RIGHT -> {
                    topRightCornerDrag(dragPoint)
                }

                RectEdge.BOTTOM_LEFT -> {
                    bottomLeftCornerDrag(dragPoint)
                }

                RectEdge.BOTTOM_RIGHT -> {
                    bottomRightCornerDrag(dragPoint)
                }

                else -> Unit
            }
        }
    }

    private fun processIRectDrag(dragPoint: Offset) {
        dragDiffCalculation(dragPoint)?.let { diffOffset ->
            val offsetCheck = Offset(
                x = (irectTopleft.x + diffOffset.x),
                y = (irectTopleft.y + diffOffset.y)
            )

            // before updating the top left point in rect need to check the irect stays inside the canvas
            val isIRectStaysInsideCanvas = isDragPointInsideTheCanvas(offsetCheck)

            // one point may reach any of corner but other way rect can still move
            if (offsetCheck.x >= 0F && offsetCheck.y >= 0F && isIRectStaysInsideCanvas) {
                updateIRectTopLeftPoint(offsetCheck)
            } else {
                // one point may reach any of corner but other way rect can still move
                val x = offsetCheck.x
                val y = offsetCheck.y
                var newOffset: Offset? = null

                if (y <= 0F && x > 0.0F && (x + iRect.size.width in 0F..canvasSize.canvasWidth)) {
                    // top side touched to edge
                    newOffset = Offset(x, 0.0F)

                } else if (x <= 0F && y > 0F && (y + iRect.size.height in 0F..canvasSize.canvasHeight)) {
                    // left side touched to edge
                    newOffset = Offset(0.0F, y)

                } else if ((x + iRect.size.width >= canvasSize.canvasWidth) && y >= 0F
                    && (y + iRect.size.height in 0F..canvasSize.canvasHeight)
                ) {
                    // right side touched to edge
                    newOffset = Offset((canvasSize.canvasWidth - iRect.size.width), y)

                } else if ((y + iRect.size.height >= canvasSize.canvasHeight) &&
                    x > 0F &&
                    (x + iRect.size.width in 0F..canvasSize.canvasWidth)
                ) {
                    // bottom side touched to edge
                    newOffset = Offset(x, (canvasSize.canvasHeight - iRect.size.height))
                }
                if (newOffset != null) {
                    updateIRectTopLeftPoint(newOffset)
                }
            }
        }
    }


    private fun topLeftCornerDrag(dragPoint: Offset) {
        dragDiffCalculation(dragPoint)?.let { dragDiff ->
            val (canvasWidth, canvasHeight) = canvasSize
            val size = iRect.size


            val x = (0f.coerceAtLeast(irectTopleft.x + dragDiff.x))
                .coerceAtMost(canvasWidth - minLimit)

            val y = (0f.coerceAtLeast(irectTopleft.y + dragDiff.y))
                .coerceAtMost(canvasHeight - minLimit)


            // Calculate new width and height based on drag direction
            val newWidth = calculateNewSize(size.width, dragDiff.x)
            val newHeight = calculateNewSize(size.height, dragDiff.y)

            irectTopleft = Offset(x, y)

            val sizeOfIRect = when (cropType) {
                CropType.FREE_STYLE -> {
                    Size(
                        width = min(newWidth, canvasWidth),
                        height = min(newHeight, canvasHeight)
                    )
                }
                else -> {
                    val sqSide = min(newWidth, canvasWidth)
                    val totalHeight = (sqSide + irectTopleft.y)
                    val diff = canvasHeight - totalHeight
                    if (diff < 0 ) {
                        irectTopleft = irectTopleft.copy(
                            y = (irectTopleft.y + diff)
                        )
                    }

                    Size(width = sqSide, height = sqSide)
                }
            }

            iRect = iRect.copy(
                topLeft = irectTopleft,
                size = sizeOfIRect
            )

            updateTouchRect()
        }
    }

    private fun calculateNewSize(currentSize: Float, dragDiff: Float): Float {
        return if (dragDiff < 0F) {
            // Dimension will increase
            (currentSize + abs(dragDiff))
        } else {
            // Dimension will reduce
            max(currentSize - abs(dragDiff), minLimit)
        }
    }

    private fun topRightCornerDrag(dragPoint: Offset) {
        dragDiffCalculation(dragPoint)?.let { dragDiff ->
            // If irect y is already at 0 and dragDiff y is negative, no need to update
            if (iRect.topLeft.y <= 0F && dragDiff.y < 0F) return

            val size = iRect.size
            val (canvasWidth, canvasHeight) = canvasSize
            val irectX = iRect.topLeft.x
            val irectY = iRect.topLeft.y

            // Calculate new width based on drag direction
            val newWidth = if (dragDiff.x < 0F) {
                (size.width - abs(dragDiff.x))
            } else (size.width + abs(dragDiff.x))

            // Limit width based on canvas boundaries
            val width = if ((newWidth + irectX) > canvasWidth) {
                canvasWidth - irectX
            } else {
                if (newWidth <= minLimit) return
                newWidth
            }

            // Calculate new height based on drag direction
            var height = if (dragDiff.y <= 0F) {
                (size.height + abs(dragDiff.y))
            } else {
                (size.height - abs(dragDiff.y))
            }

            // Limit height based on canvas boundaries
            if (height > canvasHeight) height = canvasHeight

            // Calculate new y-point within canvas boundaries
            val yLimitPoint = canvasHeight - minLimit
            var yPoint = irectY + dragDiff.y
            yPoint = if (yPoint <= 0F) 0F else {
                if (yPoint >= yLimitPoint) yLimitPoint else yPoint
            }
            // Update top-left point and rectangle size
            irectTopleft = irectTopleft.copy(y = yPoint)

            val sizeOfIRect = when (cropType) {
                CropType.FREE_STYLE -> {
                    Size(width = maxOf(minLimit, width), height = maxOf(minLimit, height))
                }
                else -> {
                    val sqSide = maxOf(minLimit, width)
                    val totalHeight = (sqSide + irectTopleft.y)
                    val diff = canvasHeight - totalHeight

                    if (diff < 0 ) {
                        irectTopleft = irectTopleft.copy(
                            y = (irectTopleft.y + diff)
                        )
                    }
                    Size(width = sqSide, height = sqSide)
                }
            }


            iRect = iRect.copy(
                topLeft = irectTopleft,
                size = sizeOfIRect
            )

            updateTouchRect()
        }
    }

    private fun bottomLeftCornerDrag(dragPoint: Offset) {
        dragDiffCalculation(dragPoint)?.let { dragDiff ->
            val canvasHeight = canvasSize.canvasHeight
            val size = iRect.size

            // For Y-Axis
            val h = (size.height + dragDiff.y)
            val height = if ((h + iRect.topLeft.y) > (canvasSize.canvasHeight)) {
                (canvasSize.canvasHeight - iRect.topLeft.y)
            } else h


            // For X-Axis
            val x = if ((iRect.topLeft.x + dragDiff.x) >= (canvasSize.canvasWidth - minLimit)) {
                canvasSize.canvasWidth - minLimit
            } else {
                val a = iRect.topLeft.x + dragDiff.x
                if (a < 0F) return
                a
            }

            // Update top-left point and rectangle size
            irectTopleft = Offset(x = if (x < 0F) 0F else x, y = iRect.topLeft.y)

            // For Irect Width
            var width = if (dragDiff.x < 0F) {
                (size.width + abs(dragDiff.x))
            } else (size.width - abs(dragDiff.x))

            if (width >= canvasSize.canvasWidth) width = canvasSize.canvasWidth

            val sizeOfIRect = when (cropType) {
                CropType.FREE_STYLE -> {
                    Size(width = maxOf(minLimit, width), height = maxOf(minLimit, height))
                }
                else -> {
                    val sqSide = maxOf(minLimit, width)
                    val totalHeight = (sqSide + irectTopleft.y)
                    val diff = canvasHeight - totalHeight

                    if (diff < 0 ) {
                        irectTopleft = irectTopleft.copy(
                            y = (irectTopleft.y + diff)
                        )
                    }

                    Size(width = sqSide, height = sqSide)
                }
            }

            iRect = iRect.copy(
                topLeft = irectTopleft,
                size = sizeOfIRect
            )

            updateTouchRect()
        }
    }

    private fun bottomRightCornerDrag(dragPoint: Offset) {
        dragDiffCalculation(dragPoint)?.let { dragDiff ->
            val canvasHeight = canvasSize.canvasHeight
            val (sizeWidth, sizeHeight) = iRect.size

            val newWidth = (sizeWidth + dragDiff.x)
                .coerceAtMost(canvasSize.canvasWidth - iRect.topLeft.x)
            val newHeight = (sizeHeight + dragDiff.y)
                .coerceAtMost(canvasSize.canvasHeight - iRect.topLeft.y)

            val sizeOfIrect = when (cropType) {
                CropType.FREE_STYLE -> {
                    Size(
                        width = newWidth.coerceAtLeast(minLimit),
                        height = newHeight.coerceAtLeast(minLimit)
                    )
                }
                else -> {
                    val sqSide = minLimit.coerceAtLeast(newWidth)
                    val totalHeight = (sqSide + irectTopleft.y)
                    val diff = canvasHeight - totalHeight

                    if (diff < 0 ) {
                        irectTopleft = irectTopleft.copy(
                            y = (irectTopleft.y + diff)
                        )
                    }
                    Size(width = sqSide, height = sqSide)
                }
            }

            // Update rectangle size
            iRect = iRect.copy(topLeft = irectTopleft, size = sizeOfIrect)
            updateTouchRect()
        }
    }


    private fun updateIRectTopLeftPoint(offset: Offset) {
        irectTopleft = Offset(
            x = offset.x,
            y = offset.y
        )

        touchAreaRectTopLeft = Offset(
            x = (irectTopleft.x + paddingForTouchRect),
            y = (irectTopleft.y + paddingForTouchRect)
        )

        iRect = iRect.copy(
            topLeft = irectTopleft
        )
        touchRect = touchRect.copy(
            topLeft = touchAreaRectTopLeft
        )
    }


    private fun isDragPointInsideTheCanvas(dragPoint: Offset): Boolean {
        val x = (dragPoint.x + iRect.size.width)
        val y = (dragPoint.y + iRect.size.height)
        return (x in 0F..canvasSize.canvasWidth && y in 0F..canvasSize.canvasHeight)
    }

    private fun dragDiffCalculation(dragPoint: Offset): Offset? {
        if (lastPointUpdated != null && lastPointUpdated != dragPoint) {
            val difference = getDiffBetweenTwoOffset(lastPointUpdated!!, dragPoint)
            lastPointUpdated = dragPoint
            // Return the difference in coordinates
            return Offset(difference.x, difference.y)
        }
        lastPointUpdated = dragPoint
        return null
    }

    private fun getDiffBetweenTwoOffset(pointOne: Offset, pointTwo: Offset): PointF {
        val dx = pointTwo.x - pointOne.x // calculate the difference in the x coordinates
        val dy = pointTwo.y - pointOne.y // calculate the difference in the y coordinates
        // pointF holds the two x,y values
        return PointF(dx, dy)
    }

    private fun getRectEdge(touchPoint: Offset): RectEdge {
        val iRectSize = iRect.size
        val topleftX = iRect.topLeft.x
        val topleftY = iRect.topLeft.y

        val rectWidth = (topleftX + iRectSize.width)
        val rectHeight = (iRect.topLeft.y + iRectSize.height)

        val padding = minLimit

        // For bottom right edge
        val width = (touchPoint.x in (rectWidth - padding..rectWidth + padding))
        val height = (touchPoint.y in (rectHeight - padding..rectHeight + padding))

        // For bottom left edge
        val widthLeft = (touchPoint.x in (topleftX - padding..topleftX + padding))

        // For top right edge
        val isOnY = (touchPoint.y in (topleftY - padding..topleftY + padding))

        // For top left edge
        val x = (touchPoint.x in (topleftX - padding..topleftX + padding))
        val y = (touchPoint.y in (topleftY - padding..topleftY + padding))


        if (width && height) {
            // BOTTOM_RIGHT edge
            return RectEdge.BOTTOM_RIGHT
        } else if (height && widthLeft) {
            // BOTTOM_LEFT edge
            return RectEdge.BOTTOM_LEFT
        } else if (width && isOnY) {
            // TOP_RIGHT edge
            return RectEdge.TOP_RIGHT
        } else if (x && y) {
            // TOP_LEFT
            return RectEdge.TOP_LEFT
        }

        return RectEdge.NULL
    }

    private fun isTouchInputInsideTheTouchRect(touchPoint: Offset): Boolean {
        val xStartPoint = touchRect.topLeft.x
        val xEndPoint = (touchRect.topLeft.x + touchRect.size.width)

        val yStartPoint = touchRect.topLeft.y
        val yEndPoint = (touchRect.topLeft.y + touchRect.size.height)

        return (touchPoint.x in xStartPoint..xEndPoint && touchPoint.y in yStartPoint..yEndPoint)
    }

    private fun getRectFromPoints(): Rect {
        val size = iRect.size
        val right = (size.width + irectTopleft.x)
        val bottom = (size.height + irectTopleft.y)
        return Rect(
            irectTopleft.x,    //left
            irectTopleft.y,    //top
            right,             //right
            bottom,            //bottom
        )
    }

}