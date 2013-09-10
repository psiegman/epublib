package nl.siegmann.epublib.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;




/**
 * Representation of a Book.
 * 
 * All resources of a Book (html, css, xml, fonts, images) are represented as Resources. See getResources() for access to these.<br/>
 * A Book as 3 indexes into these Resources, as per the epub specification.<br/>
 * <dl>
 * <dt>Spine</dt>
 * <dd>these are the Resources to be shown when a user reads the book from start to finish.</dd>
 * <dt>Table of Contents<dt>
 * <dd>The table of contents. Table of Contents references may be in a different order and contain different Resources than the spine, and often do.
 * <dt>Guide</dt>
 * <dd>The Guide has references to a set of special Resources like the cover page, the Glossary, the copyright page, etc.
 * </dl>
 * <p/>
 * The complication is that these 3 indexes may and usually do point to different pages.
 * A chapter may be split up in 2 pieces to fit it in to memory. Then the spine will contain both pieces, but the Table of Contents only the first.
 * The Content page may be in the Table of Contents, the Guide, but not in the Spine.
 * Etc.
 * <p/>

<!-- Created with Inkscape (http://www.inkscape.org/) -->

<svg id="svg2" xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#" xmlns="http://www.w3.org/2000/svg" height="568.44" width="670.93" version="1.1" xmlns:cc="http://creativecommons.org/ns#" xmlns:dc="http://purl.org/dc/elements/1.1/">

<defs id="defs4">

<marker id="Arrow1Lend" refY="0" refX="0" orient="auto">

<path id="path4761" style="marker-start:none;" d="M0,0,5-5-12.5,0,5,5,0,0z" fill-rule="evenodd" transform="matrix(-0.8,0,0,-0.8,-10,0)" stroke="#000" stroke-width="1pt"/>

</marker>

</defs>

<metadata id="metadata7">

<rdf:RDF>

<cc:Work rdf:about="">

<dc:format>image/svg+xml</dc:format>

<dc:type rdf:resource="http://purl.org/dc/dcmitype/StillImage"/>

<dc:title/>

</cc:Work>

</rdf:RDF>

</metadata>

<g id="layer1" transform="translate(-46.64286,-73.241096)">

<path id="path2985" stroke-linejoin="miter" d="m191.18,417.24c-34.136,16.047-57.505,49.066-54.479,77.983,4.5927,43.891,50.795,88.762,106.42,108.46,73.691,26.093,175.45,22.576,247.06-6.2745,42.755-17.226,76.324-53.121,79.818-87.843,3.8921-38.675-21.416-85.828-68.415-105.77-88.899-37.721-224.06-27.142-310.4,13.445z" stroke-dashoffset="0" stroke="#000" stroke-linecap="butt" stroke-miterlimit="4" stroke-dasharray="1.49193191, 2.98386382" stroke-width="0.74596596" fill="none"/>

<g id="g3879" stroke="#000" fill="none" transform="matrix(0.50688602,0,0,0.50688602,141.59593,389.57252)">

<rect id="rect3759" stroke-dashoffset="0" height="83.406" width="60.182" stroke-dasharray="none" stroke-miterlimit="4" y="126.91" x="70.173" stroke-width="0.60862"/>

<path id="path3761" stroke-linejoin="miter" d="m76.437,137.92,47.289,0" stroke-linecap="butt" stroke-width="0.89265037px"/>

<path id="path3761-8" stroke-linejoin="miter" d="m76.437,144.49,47.289,0" stroke-linecap="butt" stroke-width="0.89265037px"/>

<path id="path3761-6" stroke-linejoin="miter" d="m76.437,152.82,47.289,0" stroke-linecap="butt" stroke-width="0.89265037px"/>

<path id="path3761-8-8" stroke-linejoin="miter" d="m76.437,159.39,47.289,0" stroke-linecap="butt" stroke-width="0.89265037px"/>

<path id="path3761-2" stroke-linejoin="miter" d="m76.437,166.58,47.289,0" stroke-linecap="butt" stroke-width="0.89265037px"/>

<path id="path3761-8-2" stroke-linejoin="miter" d="m76.437,173.15,47.289,0" stroke-linecap="butt" stroke-width="0.89265037px"/>

<path id="path3761-6-6" stroke-linejoin="miter" d="m76.437,181.48,47.289,0" stroke-linecap="butt" stroke-width="0.89265037px"/>

<path id="path3761-8-8-7" stroke-linejoin="miter" d="m76.437,188.05,47.289,0" stroke-linecap="butt" stroke-width="0.89265037px"/>

<path id="path3761-2-1" stroke-linejoin="miter" d="m76.437,194.49,47.289,0" stroke-linecap="butt" stroke-width="0.89265037px"/>

<path id="path3761-8-2-0" stroke-linejoin="miter" d="m76.437,201.06,47.289,0" stroke-linecap="butt" stroke-width="0.89265037px"/>

</g>

<g id="g3879-5" stroke="#000" fill="none" transform="matrix(0.50688602,0,0,0.50688602,220.60629,374.03899)">

<rect id="rect3759-7" stroke-dashoffset="0" height="83.406" width="60.182" stroke-dasharray="none" stroke-miterlimit="4" y="126.91" x="70.173" stroke-width="0.60862"/>

<path id="path3761-26" stroke-linejoin="miter" d="m76.437,137.92,47.289,0" stroke-linecap="butt" stroke-width="0.89265037px"/>

<path id="path3761-8-5" stroke-linejoin="miter" d="m76.437,144.49,47.289,0" stroke-linecap="butt" stroke-width="0.89265037px"/>

<path id="path3761-6-1" stroke-linejoin="miter" d="m76.437,152.82,47.289,0" stroke-linecap="butt" stroke-width="0.89265037px"/>

<path id="path3761-8-8-8" stroke-linejoin="miter" d="m76.437,159.39,47.289,0" stroke-linecap="butt" stroke-width="0.89265037px"/>

<path id="path3761-2-9" stroke-linejoin="miter" d="m76.437,166.58,47.289,0" stroke-linecap="butt" stroke-width="0.89265037px"/>

<path id="path3761-8-2-2" stroke-linejoin="miter" d="m76.437,173.15,47.289,0" stroke-linecap="butt" stroke-width="0.89265037px"/>

<path id="path3761-6-6-8" stroke-linejoin="miter" d="m76.437,181.48,47.289,0" stroke-linecap="butt" stroke-width="0.89265037px"/>

<path id="path3761-8-8-7-0" stroke-linejoin="miter" d="m76.437,188.05,47.289,0" stroke-linecap="butt" stroke-width="0.89265037px"/>

<path id="path3761-2-1-4" stroke-linejoin="miter" d="m76.437,194.49,47.289,0" stroke-linecap="butt" stroke-width="0.89265037px"/>

<path id="path3761-8-2-0-2" stroke-linejoin="miter" d="m76.437,201.06,47.289,0" stroke-linecap="butt" stroke-width="0.89265037px"/>

</g>

<g id="g3879-75" stroke="#000" fill="none" transform="matrix(0.50688602,0,0,0.50688602,390.60629,376.89613)">

<rect id="rect3759-8" stroke-dashoffset="0" height="83.406" width="60.182" stroke-dasharray="none" stroke-miterlimit="4" y="126.91" x="70.173" stroke-width="0.60862"/>

<path id="path3761-5" stroke-linejoin="miter" d="m76.437,137.92,47.289,0" stroke-linecap="butt" stroke-width="0.89265037px"/>

<path id="path3761-8-56" stroke-linejoin="miter" d="m76.437,144.49,47.289,0" stroke-linecap="butt" stroke-width="0.89265037px"/>

<path id="path3761-6-7" stroke-linejoin="miter" d="m76.437,152.82,47.289,0" stroke-linecap="butt" stroke-width="0.89265037px"/>

<path id="path3761-8-8-0" stroke-linejoin="miter" d="m76.437,159.39,47.289,0" stroke-linecap="butt" stroke-width="0.89265037px"/>

<path id="path3761-2-7" stroke-linejoin="miter" d="m76.437,166.58,47.289,0" stroke-linecap="butt" stroke-width="0.89265037px"/>

<path id="path3761-8-2-4" stroke-linejoin="miter" d="m76.437,173.15,47.289,0" stroke-linecap="butt" stroke-width="0.89265037px"/>

<path id="path3761-6-6-2" stroke-linejoin="miter" d="m76.437,181.48,47.289,0" stroke-linecap="butt" stroke-width="0.89265037px"/>

<path id="path3761-8-8-7-2" stroke-linejoin="miter" d="m76.437,188.05,47.289,0" stroke-linecap="butt" stroke-width="0.89265037px"/>

<path id="path3761-2-1-78" stroke-linejoin="miter" d="m76.437,194.49,47.289,0" stroke-linecap="butt" stroke-width="0.89265037px"/>

<path id="path3761-8-2-0-7" stroke-linejoin="miter" d="m76.437,201.06,47.289,0" stroke-linecap="butt" stroke-width="0.89265037px"/>

</g>

<g id="g3879-0" stroke="#000" fill="none" transform="matrix(0.50688602,0,0,0.50688602,344.89201,451.18184)">

<rect id="rect3759-74" stroke-dashoffset="0" height="83.406" width="60.182" stroke-dasharray="none" stroke-miterlimit="4" y="126.91" x="70.173" stroke-width="0.60862"/>

<path id="path3761-7" stroke-linejoin="miter" d="m76.437,137.92,47.289,0" stroke-linecap="butt" stroke-width="0.89265037px"/>

<path id="path3761-8-3" stroke-linejoin="miter" d="m76.437,144.49,47.289,0" stroke-linecap="butt" stroke-width="0.89265037px"/>

<path id="path3761-6-2" stroke-linejoin="miter" d="m76.437,152.82,47.289,0" stroke-linecap="butt" stroke-width="0.89265037px"/>

<path id="path3761-8-8-52" stroke-linejoin="miter" d="m76.437,159.39,47.289,0" stroke-linecap="butt" stroke-width="0.89265037px"/>

<path id="path3761-2-90" stroke-linejoin="miter" d="m76.437,166.58,47.289,0" stroke-linecap="butt" stroke-width="0.89265037px"/>

<path id="path3761-8-2-3" stroke-linejoin="miter" d="m76.437,173.15,47.289,0" stroke-linecap="butt" stroke-width="0.89265037px"/>

<path id="path3761-6-6-7" stroke-linejoin="miter" d="m76.437,181.48,47.289,0" stroke-linecap="butt" stroke-width="0.89265037px"/>

<path id="path3761-8-8-7-09" stroke-linejoin="miter" d="m76.437,188.05,47.289,0" stroke-linecap="butt" stroke-width="0.89265037px"/>

<path id="path3761-2-1-1" stroke-linejoin="miter" d="m76.437,194.49,47.289,0" stroke-linecap="butt" stroke-width="0.89265037px"/>

<path id="path3761-8-2-0-4" stroke-linejoin="miter" d="m76.437,201.06,47.289,0" stroke-linecap="butt" stroke-width="0.89265037px"/>

</g>

<g id="g3879-05" stroke="#000" fill="none" transform="matrix(0.50688602,0,0,0.50688602,447.74915,459.75326)">

<rect id="rect3759-69" stroke-dashoffset="0" height="83.406" width="60.182" stroke-dasharray="none" stroke-miterlimit="4" y="126.91" x="70.173" stroke-width="0.60862"/>

<path id="path3761-40" stroke-linejoin="miter" d="m76.437,137.92,47.289,0" stroke-linecap="butt" stroke-width="0.89265037px"/>

<path id="path3761-8-6" stroke-linejoin="miter" d="m76.437,144.49,47.289,0" stroke-linecap="butt" stroke-width="0.89265037px"/>

<path id="path3761-6-3" stroke-linejoin="miter" d="m76.437,152.82,47.289,0" stroke-linecap="butt" stroke-width="0.89265037px"/>

<path id="path3761-8-8-72" stroke-linejoin="miter" d="m76.437,159.39,47.289,0" stroke-linecap="butt" stroke-width="0.89265037px"/>

<path id="path3761-2-6" stroke-linejoin="miter" d="m76.437,166.58,47.289,0" stroke-linecap="butt" stroke-width="0.89265037px"/>

<path id="path3761-8-2-9" stroke-linejoin="miter" d="m76.437,173.15,47.289,0" stroke-linecap="butt" stroke-width="0.89265037px"/>

<path id="path3761-6-6-23" stroke-linejoin="miter" d="m76.437,181.48,47.289,0" stroke-linecap="butt" stroke-width="0.89265037px"/>

<path id="path3761-8-8-7-4" stroke-linejoin="miter" d="m76.437,188.05,47.289,0" stroke-linecap="butt" stroke-width="0.89265037px"/>

<path id="path3761-2-1-36" stroke-linejoin="miter" d="m76.437,194.49,47.289,0" stroke-linecap="butt" stroke-width="0.89265037px"/>

<path id="path3761-8-2-0-9" stroke-linejoin="miter" d="m76.437,201.06,47.289,0" stroke-linecap="butt" stroke-width="0.89265037px"/>

</g>

<g id="g4373" transform="matrix(0.73826572,0,0,0.77895183,-12.385803,230.83289)">

<path id="path4359" d="m463.57,320.22,58.571,0c-6.6549-9.2417-17.897-15-29.286-15-11.388,0-22.631,5.7583-29.286,15" fill="#0F0"/>

<path id="path4363" d="m500.71,294.15-12.5,7.8571,23.929,0-11.429-7.8571" fill="#F00"/>

<rect id="rect4367" height="10.357" width="17.143" y="302.01" x="492.14" fill="#A40"/>

<rect id="rect4369" height="18.929" width="3.5714" y="296.65" x="476.43" fill="#520"/>

<path id="path4371" d="m490,292.01c0,4.1421-5.3566,7.5-11.964,7.5-6.6077,0-11.964-3.3579-11.964-7.5s5.3566-7.5,11.964-7.5c6.6077,0,11.964,3.3579,11.964,7.5z" transform="matrix(1,0,0,1.2619048,0,-78.441795)" fill="#008000"/>

<rect id="rect3759-8-0" stroke-dashoffset="0" transform="matrix(0,1,-1,0,0,0)" height="65.034" width="44.775" stroke="#000" stroke-dasharray="none" stroke-miterlimit="4" y="-525.55" x="275.43" stroke-width="0.46356" fill="none"/>

</g>

<g id="g4373-9" transform="matrix(0.73826572,0,0,0.77895183,-109.70121,291.80218)">

<path id="path4359-7" d="m463.57,320.22,58.571,0c-6.6549-9.2417-17.897-15-29.286-15-11.388,0-22.631,5.7583-29.286,15" fill="#0F0"/>

<path id="path4363-7" d="m500.71,294.15-12.5,7.8571,23.929,0-11.429-7.8571" fill="#F00"/>

<rect id="rect4367-3" height="10.357" width="17.143" y="302.01" x="492.14" fill="#A40"/>

<rect id="rect4369-7" height="18.929" width="3.5714" y="296.65" x="476.43" fill="#520"/>

<path id="path4371-9" d="m490,292.01c0,4.1421-5.3566,7.5-11.964,7.5-6.6077,0-11.964-3.3579-11.964-7.5s5.3566-7.5,11.964-7.5c6.6077,0,11.964,3.3579,11.964,7.5z" transform="matrix(1,0,0,1.2619048,0,-78.441795)" fill="#008000"/>

<rect id="rect3759-8-0-4" stroke-dashoffset="0" transform="matrix(0,1,-1,0,0,0)" height="65.034" width="44.775" stroke="#000" stroke-dasharray="none" stroke-miterlimit="4" y="-525.55" x="275.43" stroke-width="0.46356" fill="none"/>

</g>

<rect id="rect4465" height="217.14" width="137.14" stroke="#000" y="139.51" x="67.143" fill="none"/>

<rect id="rect4467" stroke-dashoffset="0" height="44.286" width="97.143" stroke="#000" stroke-dasharray="none" stroke-miterlimit="4" y="163.79" x="89.286" stroke-width="1" fill="none"/>

<rect id="rect4467-0" stroke-dashoffset="0" height="44.286" width="97.143" stroke="#000" stroke-dasharray="none" stroke-miterlimit="4" y="237.36" x="89.286" stroke-width="1" fill="none"/>

<rect id="rect4467-0-7" stroke-dashoffset="0" height="44.286" width="97.143" stroke="#000" stroke-dasharray="none" stroke-miterlimit="4" y="298.79" x="89.286" stroke-width="1" fill="none"/>

<text id="text4507" style="letter-spacing:0px;word-spacing:0px;" font-weight="normal" xml:space="preserve" font-size="40px" font-style="normal" y="122.36219" x="88.571434" font-family="Sans" line-height="125%" fill="#000000"><tspan id="tspan4509" x="88.571434" y="122.36219">Spine</tspan></text>

<rect id="rect4465-2" height="147.54" width="137.14" stroke="#000" y="162.39" x="327.14" stroke-width="0.8243" fill="none"/>

<rect id="rect4467-8" stroke-dashoffset="0" height="44.286" width="97.143" stroke="#000" stroke-dasharray="none" stroke-miterlimit="4" y="185.24" x="349.29" stroke-width="1" fill="none"/>

<rect id="rect4467-0-7-2" stroke-dashoffset="0" height="44.286" width="97.143" stroke="#000" stroke-dasharray="none" stroke-miterlimit="4" y="248.82" x="349.29" stroke-width="1" fill="none"/>

<text id="text4507-3" style="letter-spacing:0px;word-spacing:0px;" font-weight="normal" xml:space="preserve" font-size="40px" font-style="normal" y="142.38702" x="262.85712" font-family="Sans" line-height="125%" fill="#000000"><tspan id="tspan4509-8" x="262.85712" y="142.38702">Table of Contents</tspan></text>

<rect id="rect4465-9" height="163.3" width="137.14" stroke="#000" y="225.24" x="560" stroke-width="0.86719" fill="none"/>

<rect id="rect4467-4" stroke-dashoffset="0" height="44.286" width="97.143" stroke="#000" stroke-dasharray="none" stroke-miterlimit="4" y="249.53" x="582.14" stroke-width="1" fill="none"/>

<rect id="rect4467-0-8" stroke-dashoffset="0" height="44.286" width="97.143" stroke="#000" stroke-dasharray="none" stroke-miterlimit="4" y="323.1" x="582.14" stroke-width="1" fill="none"/>

<text id="text4507-5" style="letter-spacing:0px;word-spacing:0px;" font-weight="normal" xml:space="preserve" font-size="40px" font-style="normal" y="208.1013" x="581.42853" font-family="Sans" line-height="125%" fill="#000000"><tspan id="tspan4509-1" x="581.42853" y="208.1013">Guide</tspan></text>

<text id="text4577" style="letter-spacing:0px;word-spacing:0px;" font-weight="normal" xml:space="preserve" font-size="21.50233269px" font-style="normal" y="188.89537" x="92.349854" font-family="Sans" line-height="125%" fill="#000000"><tspan id="tspan4579" x="92.349854" y="188.89537">Chapter 1</tspan></text>

<text id="text4577-0" style="letter-spacing:0px;word-spacing:0px;" font-weight="normal" xml:space="preserve" font-size="21.50233269px" font-style="normal" y="255.01701" x="92.76873" font-family="Sans" line-height="125%" fill="#000000"><tspan id="tspan4579-5" x="92.76873" y="255.01701">Chapter 1</tspan></text>

<text id="text4577-0-3" style="letter-spacing:0px;word-spacing:0px;" font-weight="normal" xml:space="preserve" font-size="21.50233269px" font-style="normal" y="278.23132" x="108.66158" font-family="Sans" line-height="125%" fill="#000000"><tspan id="tspan4579-5-9" x="108.66158" y="278.23132">Part 2</tspan></text>

<text id="text4577-0-6" style="letter-spacing:0px;word-spacing:0px;" font-weight="normal" xml:space="preserve" font-size="21.50233269px" font-style="normal" y="327.33847" x="90.983017" font-family="Sans" line-height="125%" fill="#000000"><tspan id="tspan4579-5-1" x="90.983017" y="327.33847">Chapter 2</tspan></text>

<text id="text4577-6" style="letter-spacing:0px;word-spacing:0px;" font-weight="normal" xml:space="preserve" font-size="21.50233269px" font-style="normal" y="215.1956" x="351.34015" font-family="Sans" line-height="125%" fill="#000000"><tspan id="tspan4579-7" x="351.34015" y="215.1956">Chapter 1</tspan></text>

<text id="text4577-0-6-1" style="letter-spacing:0px;word-spacing:0px;" font-weight="normal" xml:space="preserve" font-size="21.50233269px" font-style="normal" y="276.62418" x="351.36185" font-family="Sans" line-height="125%" fill="#000000"><tspan id="tspan4579-5-1-0" x="351.36185" y="276.62418">Chapter 2</tspan></text>

<text id="text4577-6-5" style="letter-spacing:0px;word-spacing:0px;" font-weight="normal" xml:space="preserve" font-size="21.50233269px" font-style="normal" y="278.05276" x="598.48297" font-family="Sans" line-height="125%" fill="#000000"><tspan id="tspan4579-7-9" x="598.48297" y="278.05276">Cover</tspan></text>

<text id="text4507-1" style="letter-spacing:0px;word-spacing:0px;" font-weight="normal" xml:space="preserve" font-size="40px" font-style="normal" y="418.66241" x="238.73047" font-family="Sans" line-height="125%" fill="#000000"><tspan id="tspan4509-6" x="238.73047" y="418.66241">Resources</tspan></text>

<text id="text4577-6-5-4" style="letter-spacing:0px;word-spacing:0px;" font-weight="normal" xml:space="preserve" font-size="21.50233269px" font-style="normal" y="351.48663" x="594.909" font-family="Sans" line-height="125%" fill="#000000"><tspan id="tspan4749" x="594.909" y="351.48663">Preface</tspan></text>

<path id="path5205" stroke-linejoin="miter" style="marker-end:url(#Arrow1Lend);" d="M148.67,208.08,261.11,438.37" stroke="#000" stroke-linecap="butt" stroke-width="1px" fill="none"/>

<path id="path5207" stroke-linejoin="miter" style="marker-end:url(#Arrow1Lend);" d="M386.62,229.53,278.57,442.36" stroke="#000" stroke-linecap="butt" stroke-width="1px" fill="none"/>

<path id="path5211" stroke-linejoin="miter" style="marker-end:url(#Arrow1Lend);" d="m143.46,281.65,43.605,172.25" stroke="#000" stroke-linecap="butt" stroke-width="1px" fill="none"/>

<path id="path5213" stroke-linejoin="miter" style="marker-end:url(#Arrow1Lend);" d="M186.27,343.08,431.43,455.22" stroke="#000" stroke-linecap="butt" stroke-width="1px" fill="none"/>

<path id="path5215" stroke-linejoin="miter" style="marker-end:url(#Arrow1Lend);" d="m402.9,293.1,33.719,148.12" stroke="#000" stroke-linecap="butt" stroke-width="1px" fill="none"/>

<path id="path5219" stroke-linejoin="miter" style="marker-end:url(#Arrow1Lend);" d="M610.94,293.82,404.29,525.22" stroke="#000" stroke-linecap="butt" stroke-width="1px" fill="none"/>

<path id="path5221" stroke-linejoin="miter" style="marker-end:url(#Arrow1Lend);" d="M616.08,367.39,512.54,524.08" stroke="#000" stroke-linecap="butt" stroke-width="1px" fill="none"/>

</g>

</svg>


 * @author paul
 *
 */
public class Book implements Serializable {
	
	private static final long serialVersionUID = 2068355170895770100L;

	private Resources resources = new Resources();
	private Metadata metadata = new Metadata();
	private Spine spine = new Spine();
	private TableOfContents tableOfContents = new TableOfContents();
	private Guide guide = new Guide();
	private Resource opfResource;
	private Resource ncxResource;
	private Resource coverImage;
	
	/**
	 * Adds the resource to the table of contents of the book as a child section of the given parentSection
	 * 
	 * @param parentSection
	 * @param sectionTitle
	 * @param resource
	 * @return The table of contents
	 */
	public TOCReference addSection(TOCReference parentSection, String sectionTitle,
			Resource resource) {
		getResources().add(resource);
		if (spine.findFirstResourceById(resource.getId()) < 0)  {
			spine.addSpineReference(new SpineReference(resource));
		}
		return parentSection.addChildSection(new TOCReference(sectionTitle, resource));
	}

	public void generateSpineFromTableOfContents() {
		Spine spine = new Spine(tableOfContents);
		
		// in case the tocResource was already found and assigned
		spine.setTocResource(this.spine.getTocResource());
		
		this.spine = spine;
	}
	
	/**
	 * Adds a resource to the book's set of resources, table of contents and if there is no resource with the id in the spine also adds it to the spine.
	 * 
	 * @param title
	 * @param resource
	 * @return The table of contents
	 */
	public TOCReference addSection(String title, Resource resource) {
		getResources().add(resource);
		TOCReference tocReference = tableOfContents.addTOCReference(new TOCReference(title, resource));
		if (spine.findFirstResourceById(resource.getId()) < 0)  {
			spine.addSpineReference(new SpineReference(resource));
		}
		return tocReference;
	}
	
	
	/**
	 * The Book's metadata (titles, authors, etc)
	 * 
	 * @return The Book's metadata (titles, authors, etc)
	 */
	public Metadata getMetadata() {
		return metadata;
	}
	public void setMetadata(Metadata metadata) {
		this.metadata = metadata;
	}
	

	public void setResources(Resources resources) {
		this.resources = resources;
	}


	public Resource addResource(Resource resource) {
		return resources.add(resource);
	}
	
	/**
	 * The collection of all images, chapters, sections, xhtml files, stylesheets, etc that make up the book.
	 * 
	 * @return The collection of all images, chapters, sections, xhtml files, stylesheets, etc that make up the book.
	 */
	public Resources getResources() {
		return resources;
	}


	/**
	 * The sections of the book that should be shown if a user reads the book from start to finish.
	 * 
	 * @return The Spine
	 */
	public Spine getSpine() {
		return spine;
	}


	public void setSpine(Spine spine) {
		this.spine = spine;
	}


	/**
	 * The Table of Contents of the book.
	 * 
	 * @return The Table of Contents of the book.
	 */
	public TableOfContents getTableOfContents() {
		return tableOfContents;
	}


	public void setTableOfContents(TableOfContents tableOfContents) {
		this.tableOfContents = tableOfContents;
	}
	
	/**
	 * The book's cover page as a Resource.
	 * An XHTML document containing a link to the cover image.
	 * 
	 * @return The book's cover page as a Resource
	 */
	public Resource getCoverPage() {
		Resource coverPage = guide.getCoverPage();
		if (coverPage == null) {
			coverPage = spine.getResource(0);
		}
		return coverPage;
	}
	
	
	public void setCoverPage(Resource coverPage) {
		if (coverPage == null) {
			return;
		}
		if (! resources.containsByHref(coverPage.getHref())) {
			resources.add(coverPage);
		}
		guide.setCoverPage(coverPage);
	}
	
	/**
	 * Gets the first non-blank title from the book's metadata.
	 * 
	 * @return the first non-blank title from the book's metadata.
	 */
	public String getTitle() {
		return getMetadata().getFirstTitle();
	}
	
	
	/**
	 * The book's cover image.
	 * 
	 * @return The book's cover image.
	 */
	public Resource getCoverImage() {
		return coverImage;
	}

	public void setCoverImage(Resource coverImage) {
		if (coverImage == null) {
			return;
		}
		if (! resources.containsByHref(coverImage.getHref())) {
			resources.add(coverImage);
		}
		this.coverImage = coverImage;
	}
	
	/**
	 * The guide; contains references to special sections of the book like colophon, glossary, etc.
	 * 
	 * @return The guide; contains references to special sections of the book like colophon, glossary, etc.
	 */
	public Guide getGuide() {
		return guide;
	}

	/**
	 * All Resources of the Book that can be reached via the Spine, the TableOfContents or the Guide.
	 * <p/>
	 * Consists of a list of "reachable" resources:
	 * <ul>
	 * <li>The coverpage</li>
	 * <li>The resources of the Spine that are not already in the result</li>
	 * <li>The resources of the Table of Contents that are not already in the result</li>
	 * <li>The resources of the Guide that are not already in the result</li>
	 * </ul>
	 * To get all html files that make up the epub file use {@link #getResources()}
	 * @return All Resources of the Book that can be reached via the Spine, the TableOfContents or the Guide.
	 */
	public List<Resource> getContents() {
		Map<String, Resource> result = new LinkedHashMap<String, Resource>();
		addToContentsResult(getCoverPage(), result);

		for (SpineReference spineReference: getSpine().getSpineReferences()) {
			addToContentsResult(spineReference.getResource(), result);
		}

		for (Resource resource: getTableOfContents().getAllUniqueResources()) {
			addToContentsResult(resource, result);
		}
		
		for (GuideReference guideReference: getGuide().getReferences()) {
			addToContentsResult(guideReference.getResource(), result);
		}

		return new ArrayList<Resource>(result.values());
	}
	
	private static void addToContentsResult(Resource resource, Map<String, Resource> allReachableResources){
		if (resource != null && (! allReachableResources.containsKey(resource.getHref()))) {
			allReachableResources.put(resource.getHref(), resource);
		}
	}

	public Resource getOpfResource() {
		return opfResource;
	}
	
	public void setOpfResource(Resource opfResource) {
		this.opfResource = opfResource;
	}
	
	public void setNcxResource(Resource ncxResource) {
		this.ncxResource = ncxResource;
	}

	public Resource getNcxResource() {
		return ncxResource;
	}
}

