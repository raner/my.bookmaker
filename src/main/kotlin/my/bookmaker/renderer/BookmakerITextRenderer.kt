//                                                                            //
// My Bookmaker - Markdown-based creation of printed books                    //
// Copyright (C) 2023 Mirko Raner                                             //
//                                                                            //
// This program is free software: you can redistribute it and/or modify       //
// it under the terms of the GNU Affero General Public License as             //
// published by the Free Software Foundation, either version 3 of the         //
// License, or (at your option) any later version.                            //
//                                                                            //
// This program is distributed in the hope that it will be useful,            //
// but WITHOUT ANY WARRANTY; without even the implied warranty of             //
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the               //
// GNU Affero General Public License for more details.                        //
//                                                                            //
// You should have received a copy of the GNU Affero General Public License   //
// along with this program. If not, see <https://www.gnu.org/licenses/>.      //
//                                                                            //
package my.bookmaker.renderer

import org.codehaus.plexus.util.ReflectionUtils.setVariableValueInObject
import org.xhtmlrenderer.context.StyleReference
import org.xhtmlrenderer.layout.SharedContext
import org.xhtmlrenderer.pdf.ITextFontResolver
import org.xhtmlrenderer.pdf.ITextRenderer
import org.xhtmlrenderer.pdf.ITextReplacedElementFactory
import org.xhtmlrenderer.pdf.ITextTextRenderer
import org.xhtmlrenderer.pdf.ITextUserAgent

class BookmakerITextRenderer(listener: DrawingListener) : ITextRenderer() {
    private val defaultDotsPerPoint = 20f * 4f / 3f
    private val defaultDotsPerPixel = 20
    init {
        val dotsPerPoint = defaultDotsPerPoint
        setVariableValueInObject(this, "_dotsPerPoint", dotsPerPoint)
        val outputDevice = BookmakerITextOutputDevice(dotsPerPoint, listener)
        setVariableValueInObject(this, "_outputDevice", outputDevice)
        val userAgent = ITextUserAgent(outputDevice)
        val sharedContext = SharedContext()
        setVariableValueInObject(this, "_sharedContext", sharedContext)
        sharedContext.userAgentCallback = userAgent
        sharedContext.css = StyleReference(userAgent)
        userAgent.sharedContext = sharedContext
        outputDevice.sharedContext = sharedContext
        val fontResolver = ITextFontResolver(sharedContext)
        sharedContext.fontResolver = fontResolver
        val replacedElementFactory = ITextReplacedElementFactory(outputDevice)
        sharedContext.replacedElementFactory = replacedElementFactory
        sharedContext.textRenderer = ITextTextRenderer()
        sharedContext.dpi = 72 * dotsPerPoint
        sharedContext.dotsPerPixel = defaultDotsPerPixel
        sharedContext.isPrint = true
        sharedContext.isInteractive = false
    }
}