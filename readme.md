<H1>This is the documentation site for xmlCam.</H1>
<p>All measures here are in the metric system. Length values are always mm and velocitys mm/s if there is no other unit mentioned.</p>
<p>The software considers all contours as a gravur, that mean that it doesn't consider the diameter of the tool. The contour is in center of the tool then at the moment. For closed contours I will maybe implement (if I have enough time) a correction of the tool radius for inner our outer miiling of the contour.</p>
<p>I use for my machine the GRBL V1.1 firmware Please take into account, that other firmware could interpret the G-Code different. If you are not sure, check the documentation, how the behaviour of you firmware is.</p>
<p>I am not responsible for any damages on your machine. Please be careful by using the software in this early state, because I have to check as well if the G-Code works reliable.</p>
<h2>What is xmlCam?</h2>
<p>xmlCam is a software to generate G-Code very fast and with less effort for a CNC-Milling machine.</p>
<p>The only thing you need to do is to create the toolpathes with elements via XML-Code.
<p>The software is in an early state and comes with absolutely no warranty. I will try to continue development for xmlCam, but programming is not my profession so I have to do it in freetime. I ask for your understanding if some bugs are in the software.</p>
<p>xmlCAM is free software licensed under the GNU General Public License version 3 published by the Free Software Foundation. <a href="index.php?Itemid=106">For more information read the license here.</a></p>
<h2>Views</h2>
<h3>XML View</h3>
<p>The XML View shows the XML document which is the source for G-Code generation. You can write your XML in the textpane and generate G-Code by pressing "Generate G-Code".</p>
<p>![xmlview](https://github.com/user-attachments/assets/77f46ad3-82df-4e65-a5c5-27b806ea18e8)</p>
<p>There is a validator, which validates your XML in real time. Red font color shows mistakes and black font color shows correct code. The description of the error will displayed in a field over the XML text pane.</p>
<h3>Table view</h3>
<p>The table view show the generated G-Code. By clicking a cell you can edit the field. If your input is invalid, your input will skipped.</p>
<p><img src="images/views/tableview.png" alt="Table View" /></p>
<p>If you click the "Insert row" button, a new row will inserted above the selected row. If you click the "Delete row" button the selected row or rows (multiple selection) will deleted. By clicking "New Field" a new field will inserted in the selected row.</p>
<p>You can define start and end G-Code in two text files. This files shall placed in the same folder as the .jar file and named "start.gcode" and "end.gcode". The files will parsed and inserted to the generated G-Code automatically.</p>
<h3>Graphic View</h3>
<p>The graphic view show the generated G-Code as the name says as a graphic. The bottom sided ruler shows the x axis and the left sided ruler shows the y axis.</p>
<p>Green lines represent G0 moves with an security height for the z axis. You can define the security height in the settings.txt file. You can find more about the settings <a href="index.php?Itemid=109">here</a>.</p>
<p>The black lines represent the G1 moves, which will move at z height defined in the &lt;z&gt; tag.</p>
<p><img src="images/views/graphicview.png" alt="Graphic View" /></p>
<p>It is possible to zoom in and out by clicking the "+" and "-" button.</p>
<p>On the menubar -&gt; Graphic View it is possible to show or hide the G0 and G1 moves as well the calculated points and the grid suitable to the x and y rulers.</p>
<h2>Elements</h2>
<h3>Line Element</h3>
<p>This element generates G-Code for a line. <br />The line is defined by two points defined with &lt;p&gt; tags. The tupel in &lt;p&gt; defines the x and y position of the point (&lt;p&gt;x,y&lt;/p&gt;).<br />The z-depth must be defined by the &lt;z&gt; tag. The tupel in &lt;z&gt; defines the the start layer (workpiece surface), the end layer (depth), and the steps (&lt;z&gt;startZ,endZ,stepZ&lt;/z&gt;).</p>
<pre>&lt;line&gt;<br />  &lt;p&gt;40,200&lt;/p&gt;<br />  &lt;p&gt;340,250&lt;/p&gt;<br />  &lt;z&gt;0,-1,0.1&lt;/z&gt;<br />&lt;/line&gt;</pre>
<p>This code snippet has the following result:</p>
<p><img src="images/elements/lineelement.png" alt="A line" /></p>

<h3>Polyline-Element</h3>
<p>This element generates G-Code for a polyline.<br />The polyline is defined by two or more points. The tupel in &lt;p&gt; defines the x and y position of the point (&lt;p&gt;x,y&lt;/p&gt;). Two consecutive points descibe a line.</p>
<h3>Bezier curves</h3>
<p>You can describe a bow by setting control points. The start point (b<sub>0</sub>) and end point (b<sub>n</sub>) are defined by &lt;p&gt; tags. You need to define one ore more inner control points (b<sub>1</sub> to b<sub>n-1</sub>) with tag &lt;bez&gt;x,y&lt;/bez&gt;.<br />With one inner control point you describe a quadratic bezier curve (second grade), with two inner control points a cubic bezier curve (third grade), with n control points you describe a curve with grade n + 1.<br />For more information see the wikipedia article in <a href="https://de.wikipedia.org/wiki/B%C3%A9zierkurve" target="_blank" rel="noopener">German</a> or in <a href="https://en.wikipedia.org/wiki/B%C3%A9zier_curve" target="_blank" rel="noopener">English</a>.</p>
<pre>&lt;polyline&gt;<br />  &lt;p&gt;200,50&lt;/p&gt;<br />  &lt;p&gt;50,250&lt;/p&gt; &lt;!-- Start control point b0 of bezier curve --&gt;<br />  &lt;bez&gt;125,400&lt;/bez&gt; &lt;!-- Inner control point b1 of bezier curve --&gt;<br />  &lt;p&gt;200,250&lt;/p&gt; &lt;!-- End control point of bezier curve bn --&gt;<br />  &lt;z&gt;0,-1,0.1&lt;/z&gt;<br />&lt;/polyline&gt;</pre>
<p>This code snippet has the following result:</p>
<p><img src="images/elements/polylineelement.png" alt="A polyline" /></p>
<h3>Cubic Hermite splines</h3>
<p>The &lt;spl&gt; tag defines a spline curve which goes through the point. The spline must begin with a point (&lt;p&gt; tag) and continue with a &lt;spl&gt; tag. To influence the slope on the begin and end of the curve you can add a line short line section with the &lt;p&gt; tag at the begin and/or end like a direction vector. If you wish a closed spline you only need to set the last spline point equal to the first point.</p>
<p>See the wikipedia articles for more information in <a href="https://de.wikipedia.org/wiki/Kubisch_Hermitescher_Spline" target="_blank" rel="noopener">German</a> and in <a href="https://en.wikipedia.org/wiki/Cubic_Hermite_spline" target="_blank" rel="noopener">English</a>.</p>
<pre>&lt;polyline&gt;<br />  &lt;p&gt;50,100&lt;/p&gt;<br />  &lt;spl&gt;100,150&lt;/spl&gt;  <br />  &lt;spl&gt;100,300&lt;/spl&gt;<br />  &lt;spl&gt;300,50&lt;/spl&gt;<br />  &lt;z&gt;0,-1,0.5&lt;/z&gt;<br />&lt;/polyline&gt;</pre>
<p>This code snippet has the following result:</p>
<p><img src="images/elements/polylineelement_spline.png" alt="The spline in a polyline element" /></p>
<p>The z-depth must be defined by the &lt;z&gt; tag. The tupel in &lt;z&gt; defines the the start layer (workpiece surface), the end layer (depth), and the steps (&lt;z&gt;startZ,endZ,stepZ&lt;/z&gt;).</p>
<h3>Circle Element</h3>
<p> This element generates G-Code for a circle.<br /> A circle is defined by the center point determined through a &lt;p&gt; tag and a radius defined through a &lt;rad&gt; tag. The tupel in &lt;p&gt; defines the x and y position of the point (&lt;p&gt;x,y&lt;/p&gt;). The number in &lt;rad&gt; tag defines the radius (&lt;rad&gt;radius&lt;/rad).<br /> The z-depth must be defined by the &lt;z&gt; tag. The tupel in &lt;z&gt; defines the the start layer (workpiece surface), the end layer (depth), and the steps (&lt;z&gt;startZ,endZ,stepZ&lt;/z&gt;).</p>
<pre>&lt;circle&gt;<br />  &lt;p&gt;100,200&lt;/p&gt;<br />  &lt;rad&gt;50&lt;/rad&gt;<br />  &lt;z&gt;0,-1,0.1&lt;/z&gt;<br />&lt;/circle&gt;</pre>
<p>This code snippet has the following result:</p>
<p><img src="images/elements/circleelement.png" alt="A circle" /></p>
<h3>Feedrate Element</h3>
<p>This element sets the feedrate in mm/min for all subsequent G-Code.</p>
<pre>&lt;feedrate&gt;200&lt;/feedrate&gt;</pre>
<p>The generated G-Code for this snippet is</p>
<p>G0 F200</p>
<p> </p>
<h2>Settings</h2>
<p>You can define your own settings for xmlCam. At the moment there are only a few settings to be done available.</p>
<pre>security-height = 5;         // The security height for a G0 move above the workpiece.<br />workbench = 0, 0, 400, 400;  // The bounds of the workbench (xmin, ymin, xmax, ymax).<br />step = 50;                   // The ruler and grid steps for graphical view.</pre>
<p>You need to save this into a file named "settings.txt" located in the xmlCam main folder. If you don't define the settings, default values will loaded.</p>
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
<p>Download the xmlCAM file from <a href="index.php?Itemid=103">here</a> and extract it in a folder of your choice. Enter the folder and execute xmlCAM by running</p>
<pre>java -jar xmlCAM.jar</pre>
<h2>Build from source</h2>
<h3>Compile under Linux</h3>
<p>For Ubuntu or Debian open a console and install the build tool ant with</p>
<pre>apt-get install ant</pre>
<p>Download the source code from <a href="index.php?Itemid=103">here</a> and extract it or you can get the current vesion by clone the git repository.</p>
<pre>git clone git://github.com/bilderkiste/xmlCAM</pre>
<p>Now enter the folder where the file build.xml is located ant execute the build tool by typing</p>
<pre>ant</pre>
<p>You will find the compiled class file in the bin directory. If you want to make a executable jar file type</p>
<pre>ant makejar</pre>
<p> </p>
