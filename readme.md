<H1>This is the documentation site for xmlCAM.</H1>
<h2>What is xmlCAM?</h2>
<p>xmlCAM is a software to generate G-Code very fast and with less effort for a CNC-Milling machine.</p>
<p>The only thing you need to do is to create the toolpathes with elements via XML-Code.
<p>The software is in an early state and comes with absolutely no warranty. I will try to continue development for xmlCam, but programming is not my profession so I have to do it in freetime. I ask for your understanding if some bugs are in the software.</p>
<p>xmlCAM is free software licensed under the GNU General Public License version 3 published by the Free Software Foundation. <a href="index.php?Itemid=106">For more information read the license here.</a></p>
<p>All measures here are in the metric system. Length values are always mm and velocitys mm/s if there is no other unit mentioned.</p>
<p>I use for my machine the GRBL V1.1 firmware Please take into account, that other firmware could interpret the G-Code different. If you are not sure, check the documentation, how the behaviour of you firmware is.</p>
<p>I am not responsible for any damages on your machine. Please be careful by using the software in this early state, because I have to check as well if the G-Code works reliable.</p>
<p>The syntax for the XML has changed since version 0.100. Please see readme.md on the release to obtain the old syntax.</p>
<h2>Views</h2>
<h3>XML View</h3>
<p>The XML View shows the XML document which is the source for G-Code generation. You can write your XML in the textpane and generate G-Code by pressing "Generate G-Code".</p>
<p>There is a validator, which validates your XML in real time. Red font markers show mistakes. The description of the error will displayed in a field over the XML text pane.</p>
<h3>Table view</h3>
<p>The table view show the generated G-Code. By clicking a cell you can edit the field. If your input is invalid, your input will skipped.</p>
</p>
<p>If you click the "Insert row" button, a new row will inserted above the selected row. If you click the "Delete row" button the selected row or rows (multiple selection) will deleted. By clicking "New Field" a new field will inserted in the selected row.</p>
<p>You can define start and end G-Code in two text files. This files shall placed in the same folder as the .jar file and named "start.gcode" and "end.gcode". The files will parsed and inserted to the generated G-Code automatically.</p>
<h3>Graphic View</h3>
<p>The graphic view show the generated G-Code as the name says as a graphic. The bottom sided ruler shows the x axis and the left sided ruler shows the y axis.</p>
<p>Green lines represent G0 moves with an security height for the z axis. You can define the security height in the settings.txt file. You can find more about the settings below.</p>
<p>The black lines represent the G1 moves, which will move at z height defined in the &lt;z&gt; tag.</p>
</p>
<p>It is possible to zoom in and out by clicking the "+" and "-" button.</p>
<p>On the menubar -&gt; Graphic View it is possible to show or hide the G0 and G1 moves as well the calculated points and the grid suitable to the x and y rulers.</p>
<h2>Tools</h2>
In the tools section all tools have to be defined.

An example code snippet:
<pre>&lt;tools&gt;
&Tab;&lt;tool id&equals;&quot;t1&quot; type&equals;&quot;ballend&quot; diameter&equals;&quot;1&period;2&quot;&sol;&gt;
&Tab;&lt;tool id&equals;&quot;t2&quot; type&equals;&quot;endmill&quot; diameter&equals;&quot;2&period;5&quot;&sol;&gt;
&lt;&sol;tools&gt;</pre>
<h2>Elements</h2>
<h3>Drill element</h3>
This element generates G-Code for a drill.
The drill is defined by one point defined with a <point> tag and attributes x and y.The depth must be defined by the <depth> tag  with attributes start for upper z level end for lower z level.

An example code snippet:
 <pre>
&lt;drill&gt;
&Tab;&lt;point x&equals;&quot;40&quot; y&equals;&quot;20&quot;&sol;&gt;
&Tab;&lt;depth start&equals;&quot;0&quot; end&equals;&quot;-1&quot;&sol;&gt;
&lt;&sol;drill&gt;</pre>
<h3>Line Element</h3>
This element Generates G-Code for a line.
The line is defined by two points defined with <point> tags with attributes x and y.
The depth must be defined by the <depth> tag  with attributes start for upper z level end for lower z level and step for dive in.

An example code snippet:
<pre>&lt;line tool&equals;&quot;t1&quot;&gt;
&Tab;&lt;point x&equals;&quot;10&quot; y&equals;&quot;150&quot;&sol;&gt;
&Tab;&lt;point x&equals;&quot;10&quot; y&equals;&quot;200&quot;&sol;&gt;
&Tab;&lt;depth start&equals;&quot;0&quot; end&equals;&quot;-1&quot; step&equals;&quot;0&period;1&quot;&sol;&gt;
&lt;&sol;line&gt;
</pre>
<h3>Polyline-Element</h3>
This element generates G-Code for a polyline.
The polyline is defined by two or more points. The <point> tag must define the position of the first point with the attribute x and y. Two consecutive points describe a line.	

The depth must be defined by the <depth> tag  with attributes start for upper z level end for lower z level and step for dive in.
Standard but optional attributes in the <options> are for closed elements pocket with possible values 'parallel' and offset with possible values 'engraving', 'inset', 'outset'. 

An example code snippet:
<pre>&lt;polyline tool&equals;&quot;t2&quot;&gt;
&Tab;&lt;point x&equals;&quot;10&quot; y&equals;&quot;50&quot;&sol;&gt;
&Tab;&lt;point x&equals;&quot;10&quot; y&equals;&quot;100&quot;&sol;&gt;
&Tab;&lt;point x&equals;&quot;100&quot; y&equals;&quot;100&quot;&sol;&gt;
&Tab;&lt;point x&equals;&quot;100&quot; y&equals;&quot;10&quot;&sol;&gt;
&Tab;&lt;depth start&equals;&quot;0&quot; end&equals;&quot;-1&quot; step&equals;&quot;0&period;1&quot;&sol;&gt;
&Tab;&lt;options pocket&equals;&quot;parallel&quot; offset&equals;&quot;inset&quot;&sol;&gt;
&lt;&sol;polyline&gt;</pre>
<h4>Bezier curves</h4>
A Bezier curve can be described with setting control points. The start point (b0) and end point (bn) are defined by <point> tags. 
One ore more inner control points can be defined (b1 to bn-1) with <bezier> tag with attributes x and y.
One inner control point describes a quadratic bezier curve (second grade), two inner control points describes a cubic bezier curve (third grade). More than two points with n control points describes a curve with grade n + 1.
For more information see in German https://de.wikipedia.org/wiki/B%C3%A9zierkurve and in English https://en.wikipedia.org/wiki/B%C3%A9zier_curve.

An example code snippet:
<pre>&lt;polyline tool&equals;&quot;t2&quot;&gt;
&Tab;&lt;point x&equals;&quot;0&quot; y&equals;&quot;50&quot;&sol;&gt;
&Tab;&lt;bezier x&equals;&quot;10&quot; y&equals;&quot;100&quot;&sol;&gt;
&Tab;&lt;bezier x&equals;&quot;100&quot; y&equals;&quot;100&quot;&sol;&gt;
&Tab;&lt;point x&equals;&quot;100&quot; y&equals;&quot;10&quot;&sol;&gt;
&Tab;&lt;depth start&equals;&quot;0&quot; end&equals;&quot;-1&quot; step&equals;&quot;1&quot;&sol;&gt;
&Tab;&lt;options pocket&equals;&quot;parallel&quot; offset&equals;&quot;inset&quot;&sol;&gt;
&lt;&sol;polyline&gt;</pre>
<h4>Cubic Hermite splines</h4>
<p>The &lt;spl&gt; tag defines a spline curve which goes through the point. The spline must begin with a point (&lt;p&gt; tag) and continue with a &lt;spl&gt; tag. To influence the slope on the begin and end of the curve you can add a line short line section with the &lt;p&gt; tag at the begin and/or end like a direction vector. If you wish a closed spline you only need to set the last spline point equal to the first point.</p>
<p>See the wikipedia articles for more information in <a href="https://de.wikipedia.org/wiki/Kubisch_Hermitescher_Spline" target="_blank" rel="noopener">German</a> and in <a href="https://en.wikipedia.org/wiki/Cubic_Hermite_spline" target="_blank" rel="noopener">English</a>.</p>

An example code snippet:
<pre>&lt;polyline tool&equals;&quot;t2&quot;&gt;
&Tab;&lt;point x&equals;&quot;10&quot; y&equals;&quot;50&quot;&sol;&gt;
&Tab;&lt;spline x&equals;&quot;10&quot; y&equals;&quot;100&quot;&sol;&gt;
&Tab;&lt;spline x&equals;&quot;100&quot; y&equals;&quot;100&quot;&sol;&gt;
&Tab;&lt;spline x&equals;&quot;100&quot; y&equals;&quot;10&quot;&sol;&gt;
&Tab;&lt;depth start&equals;&quot;0&quot; end&equals;&quot;-1&quot; step&equals;&quot;0&period;1&quot;&sol;&gt;
&Tab;&lt;options pocket&equals;&quot;parallel&quot; offset&equals;&quot;inset&quot;&sol;&gt;
&lt;&sol;polyline&gt;</pre>
</p>
<p>The z-depth must be defined by the &lt;z&gt; tag. The tupel in &lt;z&gt; defines the the start layer (workpiece surface), the end layer (depth), and the steps (&lt;z&gt;startZ,endZ,stepZ&lt;/z&gt;).</p>
<h3>Circle Element</h3>
<p>This element generates G-Code for a circle.</p>
A circle is defined by the center determined through a &gt;center&lt; tag with attributes x and y and radius defined through a <radius> tag with a value attribute.
The depth must be defined by the <depth> tag  with attributes start for upper z level end for lower z level and step for dive in.
Optional attributes in the <options> tag are segments for the definition of the number of segments i.e. 6 for an hexagon. 
Standard but optional attributes in the <options> are for closed elements pocket with possible values 'parallel' and offset with possible values 'engraving', 'inset', 'outset'. 

An example code snippet:
<pre>&lt;circle tool&equals;&quot;t2&quot;&gt;
&Tab;&lt;center x&equals;&quot;60&quot; y&equals;&quot;30&quot;&sol;&gt;
&Tab;&lt;radius value&equals;&quot;20&quot;&sol;&gt; 
&Tab;&lt;depth start&equals;&quot;0&quot; end&equals;&quot;-1&quot; step&equals;&quot;0&period;1&quot;&sol;&gt;
&Tab;&lt;options segments&equals;&quot;5&quot; offset&equals;&quot;inset&quot; pocket&equals;&quot;parallel&quot;&sol;&gt;
&lt;&sol;circle&gt;</pre>
<h3>Rectangle Element</h3>
This element generates G-Code for a rectangle.
A rectangle is defined by two points for the diagonal edges determined through two <point> tags with attributes x and y.
The depth must be defined by the <depth> tag  with attributes start for upper z level end for lower z level and step for dive in.
Optional attributes in the <options> tag are size for font size in point, font for font family, style for bold or italic styles and flatness for accuracy. 
Standard but optional attributes in the <options> are for closed elements pocket with possible values 'parallel' and offset with possible values 'engraving', 'inset', 'outset'. 

An example code snippet:
<pre>&lt;rectangle tool&equals;&quot;t2&quot;&gt;
&Tab;&lt;point x&equals;&quot;10&quot; y&equals;&quot;10&quot;&sol;&gt;
&Tab;&lt;point x&equals;&quot;30&quot; y&equals;&quot;30&quot;&sol;&gt;
&Tab;&lt;depth start&equals;&quot;0&quot; end&equals;&quot;-1&quot; step&equals;&quot;0&period;1&quot;&sol;&gt;
&Tab;&lt;options pocket&equals;&quot;parallel&quot; offset&equals;&quot;inset&quot;&sol;&gt;
&lt;&sol;rectangle&gt;</pre>
<h3>Text Element</h3>
This element generates G-Code for a text.
The Text must defined by the <content> tag.
The position of the text is determined through a <point> tag with attributes x and y defining bottom left.
The depth must be defined by the <depth> tag  with attributes start for upper z level end for lower z level and step for dive in.
Optional attributes in the <options> tag are size for font size in point, font for font family, style for bold or italic styles and flatness for accuracy. 
Standard but optional attributes in the <options> are for closed elements pocket with possible values 'parallel' and offset with possible values 'engraving', 'inset', 'outset'. 

An example code snippet:
<pre>&lt;text tool&equals;&quot;t1&quot;&gt;
&Tab;&lt;content&gt;Guten Morgen&excl;&lt;&sol;content&gt;
&Tab;&lt;point x&equals;&quot;10&quot; y&equals;&quot;50&quot;&sol;&gt;
&Tab;&lt;depth start&equals;&quot;0&quot; end&equals;&quot;-1&quot; step&equals;&quot;0&period;1&quot;&sol;&gt;
&Tab;&lt;options size&equals;&quot;20&quot; font&equals;&quot;C059&quot; style&equals;&quot;bold&quot; flatness&equals;&quot;0&period;1&quot; offset&equals;&quot;outset&quot;&sol;&gt;
&lt;&sol;text&gt;</pre>

<h3>Feedrate Element</h3>
<p>This element sets the feedrate in mm/min for all subsequent G-Code.</p>

An example code snippet:
<pre>&lt;feedrate&gt;200&lt;/feedrate&gt;</pre>
<p>The generated G-Code for this snippet is</p>
<p>G0 F200</p>
<h3>Translation Element</h3>
This element shifts the elements within this tag through the x and y pane. It is possible to use the translation tag recursive.

An example code snippet:
<pre>&lt;translate x&equals;&quot;100&quot; y&equals;&quot;50&quot;&gt;
&Tab;&lt;circle tool&equals;&quot;t2&quot;&gt;
&Tab;&Tab;&lt;center x&equals;&quot;60&quot; y&equals;&quot;30&quot; &sol;&gt;
&Tab;&Tab;&lt;radius value&equals;&quot;20&quot; &sol;&gt; 
&Tab;&Tab;&lt;depth start&equals;&quot;0&quot; end&equals;&quot;-1&quot; step&equals;&quot;0&period;1&quot; &sol;&gt;
&Tab;&Tab;&lt;options segments&equals;&quot;5&quot; offset&equals;&quot;inset&quot; pocket&equals;&quot;parallel&quot; &sol;&gt;
&Tab;&lt;&sol;circle&gt;
&Tab;&lt;translate x&equals;&quot;-20&quot; y&equals;&quot;0&quot;&gt;  
&Tab;&Tab;...
&Tab;&lt;&sol;translate&gt;
&Tab;...
&lt;&sol;translate&gt;
</pre>
The center of the circle in this examle is now at (120,70).
<h3>Offset Pathes</h3>
It is possible to create offset pathes regarding the tool diameter. Those offsets can defined with the offset attribute in the <options> tag with values inset, outset or engraving.
An example.
<pre>&lt;options &period;&period;&period; offset&equals;&quot;outset&quot; &period;&period;&period;</pre>
<h3>Pockets</h3>
It is possible to create pockets by adding the pocket attribute to the <options> tag. Possible values are parallel.
Pockets work for circle, rectangle and polyline elements.
<pre>&lt;options &period;&period;&perYou can  iod; pocket&equals;&quot;parallel&quot; &period;&period;&period;</pre>
<h2>Settings</h2>
<p>It is possible to define own settings for xmlCAM. These are defined in a yaml file. At the moment there are only a few settings available.</p>
<pre>dialect: GRBL               # The dialect for the G-Code
security-height: 5          # The security height for a G0 move above the workpiece.
workbench: [0,0,400,400] # The dimension of the workbench (xmin, ymin, xmax, ymax).
grid-step: 50               # The ruler and grid steps for graphical view.
font-size: 18               # Font size for the XML View
standard-dir: /home/test/xmlCAM # Standard directory for XML and G-Code</pre>
<p>It is neccesary to save the settings in a file named "settings.yaml" located in the xmlCAM main folder. If no settings are defined, default values will loaded.</p>
<h2>Dialects</h2>
<p>Dialects are neccessary to customize some G-Code blocks, as start, end
and toolchange G-Code to the firmware of the CNC-Machine. A dialect
must be saved in the directory ./dialects/ as a YAML file with name
&lt;dialect&gt;.yaml i.e. GRBL.yaml.</p>
<p>An example dialect file:</p>
<pre>name: GRBL

sections:
start:
- G90 ; Absolute positioning, just in case
- G21 ; Set units to mm
- G00 Z6.0000 F500 ; Raise Z 5mm to clear clamps and screws
- M03 S24000 ; Set spindle speed
end:
- M5 ; Stop spindle
toolchange:
- M5 ; Toolchange stop spindle
- G0 Z10 ; Toolchange lift up
- M0 ; Toolchange pause
- M03 S24000 ; Toolchange Set spindle speed
</pre>
<p>Please take into account, that only spaces and NO TABS allowed in YAML files.</p>
<h2>Installation</h2>
<h4>Installation under Linux</h4>
<p>To run the .jar file you need to install the java virtual machine.</p>
<p>Check if the Java Runtime Environment is installed correctly open a console and execute</p>
<pre>java</pre>
<p>If the java help appears, java is installed correctly, if not you need to install the Java Runtime Environment with command</p>
<pre>apt-get install openjdk-11-jre</pre>
<p>Download the xmlCAM file from <a href="index.php?Itemid=103">here</a> and extract it in a folder of your choice. Enter the folder and execute xmlCAM by running</p>
<pre>java -jar xmlCAM.jar</pre>
<h4>Installation under Windows</h4>
<p>To run the .jar file you need to install the java virtual machine version 1.8 or higher.</p>
<p>Check if the Java Runtime Environment is installed correctly open a console by typing <code>cmd</code> and execute</p>
<p>java.exe</p>
<p>If the java help appears, java is installed correctly, if not you need to install the Java Runtime Environment, Download the machine suitable to your computer from <a href="https://www.java.com/de/download" target="_blank" rel="noopener">https://www.java.com/de/download </a>and install the software.</p>
<p>Download the xmlCAM file here</a> and extract it in a folder of your choice. Enter the folder and execute xmlCAM by running</p>
<pre>java -jar xmlCAM.jar</pre>
<h2>Build from source</h2>
<h3>Compile under Linux</h3>
<p>For Ubuntu or Debian open a console and install the build tool ant with</p>
<pre>apt-get install ant</pre>
<p>Download the source code and extract it or you can get the current version by clone the git repository.</p>
<pre>git clone https://github.com/bilderkiste/xmlCAM</pre>
<p>Now enter the folder where the file build.xml is located ant execute the build tool by typing</p>
<pre>ant</pre>
<p>You will find the compiled class file in the bin directory.</p>
<p>It is possible to execute with</p>
<pre>java -cp "bin:lib/rsyntaxtextarea-3.6.0.jar:lib/snakeyaml-2.5.jar" main.Main</pre>
<p> If you want to make a executable jar file type</p>
<pre>ant makejar</pre>
<p> </p>
<h2>A complete program milling a front panel.</h2>
<pre>&lt;?xml version=&quot;1.0&quot;?&gt;
&lt;program&gt;
	&lt;tools&gt;
		&lt;tool id=&quot;t1&quot; diameter=&quot;2.2&quot;/&gt;
	&lt;/tools&gt;
	&lt;feedrate&gt;200&lt;/feedrate&gt;
	&lt;translate x=&quot;10&quot; y=&quot;10&quot;&gt;
		&lt;!-- Gehäuse --&gt;
		&lt;polyline tool=&quot;t1&quot;&gt;
			&lt;point x=&quot;0&quot; y=&quot;2&quot;/&gt;
			&lt;point x=&quot;0&quot; y=&quot;76&quot;/&gt;
			&lt;spline x=&quot;2&quot; y=&quot;78&quot;/&gt;
			&lt;point x=&quot;76&quot; y=&quot;78&quot;/&gt;
			&lt;spline x=&quot;78&quot; y=&quot;76&quot;/&gt;
			&lt;point x=&quot;78&quot; y=&quot;2&quot;/&gt;
			&lt;spline x=&quot;76&quot; y=&quot;0&quot;/&gt;
			&lt;point x=&quot;2&quot; y=&quot;0&quot;/&gt;
			&lt;spline x=&quot;0&quot; y=&quot;2&quot;/&gt;
			&lt;depth start=&quot;0&quot; end=&quot;-4&quot; step=&quot;0.4&quot;/&gt;
			&lt;options offset=&quot;outset&quot;/&gt;
		&lt;/polyline&gt;
		&lt;!-- Display --&gt;
		&lt;translate x=&quot;10&quot; y=&quot;10&quot;&gt;
			&lt;rectangle tool=&quot;t1&quot;&gt;
				&lt;point x=&quot;1.55&quot; y=&quot;8.65&quot;/&gt;
				&lt;point x=&quot;23.45&quot; y=&quot;20.05&quot;/&gt;
				&lt;depth start=&quot;0&quot; end=&quot;-4&quot; step=&quot;0.4&quot;/&gt;
			&lt;/rectangle&gt;
			&lt;drill tool=&quot;t1&quot;&gt;
				&lt;point x=&quot;2.25&quot; y=&quot;1.85&quot;/&gt;
				&lt;depth start=&quot;0&quot; end=&quot;-4&quot;/&gt;
			&lt;/drill&gt;
			&lt;drill tool=&quot;t1&quot;&gt;
				&lt;point x=&quot;2.25&quot; y=&quot;24.85&quot;/&gt;
				&lt;depth start=&quot;0&quot; end=&quot;-4&quot;/&gt;
			&lt;/drill&gt;
			&lt;drill tool=&quot;t1&quot;&gt;
				&lt;point x=&quot;22.75&quot; y=&quot;1.85&quot;/&gt;
				&lt;depth start=&quot;0&quot; end=&quot;-4&quot;/&gt;
			&lt;/drill&gt;
			&lt;drill tool=&quot;t1&quot;&gt;
				&lt;point x=&quot;22.75&quot; y=&quot;24.85&quot;/&gt;
				&lt;depth start=&quot;0&quot; end=&quot;-4&quot;/&gt;
			&lt;/drill&gt;
		&lt;/translate&gt;
		&lt;!-- Taster --&gt;
		&lt;translate x=&quot;50&quot; y=&quot;15.5&quot;&gt;
			&lt;drill tool=&quot;t1&quot;&gt;
				&lt;point x=&quot;0&quot; y=&quot;0&quot;/&gt;
				&lt;depth start=&quot;0&quot; end=&quot;-4&quot;/&gt;
			&lt;/drill&gt;
			&lt;drill tool=&quot;t1&quot;&gt;
				&lt;point x=&quot;16&quot; y=&quot;0&quot;/&gt;
				&lt;depth start=&quot;0&quot; end=&quot;-4&quot;/&gt;
			&lt;/drill&gt;
			&lt;circle tool=&quot;t1&quot;&gt;
				&lt;center x=&quot;8&quot; y=&quot;0&quot;/&gt;
				&lt;radius value=&quot;3&quot;/&gt;
				&lt;depth start=&quot;0&quot; end=&quot;-4&quot; step=&quot;0.4&quot;/&gt;
			&lt;/circle&gt;
			&lt;drill tool=&quot;t1&quot;&gt;
				&lt;point x=&quot;0&quot; y=&quot;16&quot;/&gt;
				&lt;depth start=&quot;0&quot; end=&quot;-4&quot;/&gt;
			&lt;/drill&gt;
			&lt;drill tool=&quot;t1&quot;&gt;
				&lt;point x=&quot;16&quot; y=&quot;16&quot;/&gt;
				&lt;depth start=&quot;0&quot; end=&quot;-4&quot;/&gt;
			&lt;/drill&gt;
			&lt;circle tool=&quot;t1&quot;&gt;
				&lt;center x=&quot;8&quot; y=&quot;16&quot;/&gt;
				&lt;radius value=&quot;3&quot;/&gt;
				&lt;depth start=&quot;0&quot; end=&quot;-4&quot; step=&quot;0.4&quot;/&gt;
			&lt;/circle&gt;	
		&lt;/translate&gt;
	  	&lt;translate x=&quot;12.5&quot; y=&quot;45&quot;&gt;
	      	&lt;circle tool=&quot;t1&quot;&gt;
	              	&lt;center x=&quot;10&quot; y=&quot;10&quot;/&gt;
	              	&lt;radius value=&quot;8.9&quot;/&gt;
	             	&lt;depth start=&quot;0&quot; end=&quot;-4&quot; step=&quot;0.4&quot;/&gt;
	      	&lt;/circle&gt;
	      	&lt;drill tool=&quot;t1&quot;&gt;
	              	&lt;point x=&quot;20&quot; y=&quot;10&quot;/&gt;
	              	&lt;depth start=&quot;0&quot; end=&quot;-4&quot;/&gt;
	      	&lt;/drill&gt;
	   	&lt;/translate&gt;
	&lt;/translate&gt;
&lt;/program&gt;</pre>