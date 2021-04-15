package com.eye.cool.photo.params

/**
 *Created by ycb on 2019/8/8 0008
 */
class ImageParams private constructor() {

  internal var cutAble = true

  internal var outputW = 300

  internal var outputH = 300

  class Builder {

    private var params = ImageParams()

    /**
     *  Whether the selected picture needs to be cut，default true
     *
     *  [cutAble] default true
     */
    fun cutAble(cutAble: Boolean): Builder {
      params.cutAble = cutAble
      return this
    }

    /**
     * Output the size of the image
     *
     * [outputW] default 300
     * [outputH] default 300
     */
    fun output(outputW: Int, outputH: Int): Builder {
      params.outputW = outputW
      params.outputH = outputH
      return this
    }

    fun build() = params
  }
}