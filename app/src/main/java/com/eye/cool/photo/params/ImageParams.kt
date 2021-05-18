package com.eye.cool.photo.params

/**
 *Created by ycb on 2019/8/8 0008
 */
class ImageParams private constructor(
    internal val cutAble: Boolean,
    internal val outputW: Int,
    internal val outputH: Int
) {

  companion object {
    inline fun build(block: Builder.() -> Unit) = Builder().apply(block).build()
  }

  data class Builder(
      var cutAble: Boolean = true,
      var outputW: Int = 300,
      var outputH: Int = 300
  ) {

    /**
     *  Whether the selected picture needs to be cut，default true
     *
     *  [cutAble] default true
     */
    fun cutAble(cutAble: Boolean) = apply { this.cutAble = cutAble }

    /**
     * Output the size of the image
     *
     * [outputW] default 300
     * [outputH] default 300
     */
    fun output(outputW: Int, outputH: Int) = apply {
      this.outputW = outputW
      this.outputH = outputH
      return this
    }

    fun build() = ImageParams(
        cutAble = cutAble,
        outputW = outputW,
        outputH = outputH
    )
  }
}