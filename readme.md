# This is the documentation site for xmlCAM.

## What is xmlCAM?

xmlCAM is a software to generate G-Code very fast and with less effort for a CNC milling machine.

The only thing you need to do is to create the toolpaths with elements via XML code.

The software is in an early state and comes with absolutely no warranty. I will try to continue development for xmlCAM, but programming is not my profession, so I have to do it in my free time. Please be understanding if there are bugs in the software.

xmlCAM is free software licensed under the **GNU General Public License version 3** published by the Free Software Foundation.

All measures are in the metric system. Length values are always **mm** and velocities **mm/s**, unless otherwise stated.

I use **GRBL v1.1** firmware. Other firmware may interpret G-Code differently. In this case see sechtion Dialects.

I am not responsible for any damages on your machine. Please be careful by using the software in this early state, because I have to check as well if the G-Code works reliable.

The syntax for the XML has changed since version 0.100. Please see readme.md on the release to obtain the old syntax.

## Views

### XML View

The XML View shows the XML document which is the source for G-Code generation. You can write your XML in the textpane and generate G-Code by pressing "Generate G-Code".

There is a validator, which validates your XML in real time. Red font markers show mistakes. The description of the error will displayed in a field over the XML text pane.

### Table View

The table view shows the generated G-Code. By clicking a cell you can edit the field. If your input is invalid, your input will skipped.

If you click the "Insert row" button, a new row will inserted above the selected row. If you click the "Delete row" button the selected row or rows (multiple selection) will deleted. By clicking "New Field" a new field will inserted in the selected row.

You can define start and end G-Code in two text files. This files shall placed in the same folder as the .jar file and named "start.gcode" and "end.gcode". The files will parsed and inserted to the generated G-Code automatically.

### Graphic View

The graphic view show the generated G-Code as the name says as a graphic. The bottom sided ruler shows the x axis and the left sided ruler shows the y axis.

Green lines represent G0 moves with an security height for the z axis. You can define the security height in the settings.txt file. You can find more about the settings below.</p>

The black lines represent the G1 moves, which will move at z height defined in the &lt;z&gt; tag.

It is possible to zoom in and out by clicking the "+" and "-" button.

On the menubar -&gt; Graphic View it is possible to show or hide the G0 and G1 moves as well the calculated points and the grid suitable to the x and y rulers.

## Tools

In the tools section all tools have to be defined.

An example code snippet:

```xml
<tools>
	<tool id="t1" type="ballend" diameter="1.2"/>
	<tool id="t2" type="endmill" diameter="2.5"/>
</tools>
```

## Elements

### Drill Element

This element generates G-Code for a drill.
The drill is defined by one point defined with a <point> tag and attributes x and y.The depth must be defined by the <depth> tag  with attributes start for upper z level end for lower z level.

An example code snippet:

```xml
<drill tool="t1">
	<point x="200" y="150"/>
	<depth start="0" end="-1"/>
</drill>
```
### Line Element

This element Generates G-Code for a line.
The line is defined by two points defined with <point> tags with attributes x and y.
The depth must be defined by the <depth> tag  with attributes start for upper z level end for lower z level and step for dive in.

```xml
<line tool="t1">
	<point x="10" y="10"/>
	<point x="50" y="10"/>
	<depth start="0" end="-1" step="1"/>
</line>
```

### Polyline Element

This element generates G-Code for a polyline.
The polyline is defined by two or more points. The <point> tag must define the position of the first point with the attribute x and y. Two consecutive points describe a line.	

The depth must be defined by the <depth> tag  with attributes start for upper z level end for lower z level and step for dive in.
Standard but optional attributes in the <options> are for closed elements pocket with possible values 'parallel' and offset with possible values 'engraving', 'inset', 'outset'. 

An example code snippet:

```xml
<polyline tool="t2">
	<point x="10" y="50"/>
	<point x="10" y="100"/>
	<point x="100" y="100"/>
	<point x="100" y="10"/>
	<depth start="0" end="-1" step="0.1"/>
	<options pocket="parallel" offset="inset"/>
</polyline>
```

#### Bezier curves

A Bezier curve can be described with setting control points. The start point (b0) and end point (bn) are defined by <point> tags. 
One ore more inner control points can be defined (b1 to bn-1) with <bezier> tag with attributes x and y.
One inner control point describes a quadratic bezier curve (second grade), two inner control points describes a cubic bezier curve (third grade). More than two points with n control points describes a curve with grade n + 1.
For more information see in [German](https://de.wikipedia.org/wiki/B%C3%A9zierkurve) and in [English](https://en.wikipedia.org/wiki/B%C3%A9zier_curve).

```xml
<polyline tool="t2">
	<point x="0" y="50"/>
	<bezier x="10" y="100"/>
	<bezier x="100" y="100"/>
	<point x="100" y="10"/>
	<depth start="0" end="-1" step="1"/>
	<options pocket="parallel" offset="inset"/>
</polyline>
```

#### Cubic Hermite splines

The &lt;spl&gt; tag defines a spline curve which goes through the point. The spline must begin with a point (&lt;p&gt; tag) and continue with a &lt;spl&gt; tag. To influence the slope on the begin and end of the curve you can add a line short line section with the &lt;p&gt; tag at the begin and/or end like a direction vector. If you wish a closed spline you only need to set the last spline point equal to the first point.
<p>See the wikipedia articles for more information in [German](https://de.wikipedia.org/wiki/Kubisch_Hermitescher_Spline) and in [English](https://en.wikipedia.org/wiki/Cubic_Hermite_spline).

```xml
<polyline tool="t2">
	<point x="10" y="50"/>
	<spline x="10" y="100"/>
	<spline x="100" y="100"/>
	<spline x="100" y="10"/>
	<depth start="0" end="-1" step="0.1"/>
	<options pocket="parallel" offset="inset"/>
</polyline>
```

### Circle Element

This element generates G-Code for a circle.

A circle is defined by the center determined through a &gt;center&lt; tag with attributes x and y and radius defined through a <radius> tag with a value attribute
.
The depth must be defined by the <depth> tag  with attributes start for upper z level end for lower z level and step for dive in.

Optional attributes in the <options> tag are segments for the definition of the number of segments i.e. 6 for an hexagon. 

Standard but optional attributes in the <options> are for closed elements pocket with possible values 'parallel' and offset with possible values 'engraving', 'inset', 'outset'. 

```xml
<circle tool="t2">
	<center x="60" y="30"/>
	<radius value="20"/> 
	<depth start="0" end="-1" step="0.1"/>
	<options segments="5" offset="inset" pocket="parallel"/>
</circle>
```

### Rectangle Element

This element generates G-Code for a rectangle.

A rectangle is defined by two points for the diagonal edges determined through two <point> tags with attributes x and y.

The depth must be defined by the <depth> tag  with attributes start for upper z level end for lower z level and step for dive in.

Optional attributes in the <options> tag are size for font size in point, font for font family, style for bold or italic styles and flatness for accuracy. 

Standard but optional attributes in the <options> are for closed elements pocket with possible values 'parallel' and offset with possible values 'engraving', 'inset', 'outset'. 

```xml
<rectangle tool="t2">
	<point x="10" y="10"/>
	<point x="30" y="30"/>
	<depth start="0" end="-1" step="0.1"/>
	<options pocket="parallel" offset="inset"/>
</rectangle>
```

### Text Element

This element generates G-Code for a text.

The Text must defined by the <content> tag.
The position of the text is determined through a <point> tag with attributes x and y defining bottom left.

The depth must be defined by the <depth> tag  with attributes start for upper z level end for lower z level and step for dive in.

Optional attributes in the <options> tag are size for font size in point, font for font family, style for bold or italic styles and flatness for accuracy. 

Standard but optional attributes in the <options> are for closed elements pocket with possible values 'parallel' and offset with possible values 'engraving', 'inset', 'outset'. 

An example code snippet:

```xml
<text tool="t1">
	<content>Guten Morgen!</content>
	<point x="10" y="50"/>
	<depth start="0" end="-1" step="0.1"/>
	<options size="20" font="C059" style="bold" flatness="0.1" offset="outset"/>
</text>
```

### Feedrate Element

This element sets the feedrate in mm/min for all subsequent G-Code.

An example code snippet:

```xml
<feedrate>200</feedrate>
```

### Translation Element

This element shifts the elements within this tag through the x and y pane. It is possible to use the translation tag recursive.

An example code snippet:

```xml
<translate x="100" y="50">
	<circle tool="t2">
		<center x="60" y="30" />
		<radius value="20" /> 
		<depth start="0" end="-1" step="0.1" />
		<options segments="5" offset="inset" pocket="parallel" />
	</circle>
	<translate x="-20" y="0">  
		...
	</translate>
	...
</translate>
```

### Offset Pathes

It is possible to create offset pathes regarding the tool diameter. Those offsets can defined with the offset attribute in the <options> tag with values inset, outset or engraving.

An example:

```xml
<options ... offset="outset" .../>
```

### Pockets

It is possible to create pockets by adding the pocket attribute to the <options> tag. Possible values are parallel.
Pockets work for circle, rectangle and polyline elements.

```xml
<options ... pocket="parallel" .../>
```

## Settings

It is possible to define own settings for xmlCAM. These are defined in a yaml file. At the moment there are only a few settings available.

```yaml
dialect: GRBL               # The dialect for the G-Code
security-height: 5          # The security height for a G0 move above the workpiece.
workbench: [0,0,400,400] # The dimension of the workbench (xmin, ymin, xmax, ymax).
grid-step: 50               # The ruler and grid steps for graphical view.
font-size: 18               # Font size for the XML View
standard-dir: /home/test/xmlCAM # Standard directory for XML and G-Code</pre>
```

It is neccesary to save the settings in a file named "settings.yaml" located in the xmlCAM main folder. If no settings are defined, default values will loaded.

## Dialects

Dialects are neccessary to customize some G-Code blocks, as start, end and toolchange G-Code to the firmware of the CNC-Machine. A dialect must be saved in the directory ./dialects/ as a YAML file with name

```yaml
name: GRBL

sections:
  start:
    - G90
    - G21
    - G00 Z6.0000 F500
    - M03 S24000
  end:
    - M5
  toolchange:
    - M5
    - G0 Z10
    - M0
    - M03 S24000
```

Please take into account, that only spaces and NO TABS allowed in YAML files.

## Installation

### Installation under Linux

To run the .jar file you need to install the java virtual machine.

Check if the Java Runtime Environment is installed correctly open a console and execute

```bash
java
```

If the java help appears, java is installed correctly, if not you need to install the Java Runtime Environment with command

```bash
apt-get install openjdk-11-jre
```

Download the xmlCAM file from here and extract it in a folder of your choice. Enter the folder and execute xmlCAM by running

```bash
java -jar xmlCAM.jar
```

### Installation under Windows

To run the .jar file you need to install the java virtual machine version 1.8 or higher.

Check if the Java Runtime Environment is installed correctly open a console by typing cmd and execute

```bash
java.exe
```

If the java help appears, java is installed correctly, if not you need to install the Java Runtime Environment, Download the machine suitable to your computer from https://www.java.com/de/download and install the software.

Download the xmlCAM file here and extract it in a folder of your choice. Enter the folder and execute xmlCAM by running

```bash
java -jar xmlCAM.jar
```

## Build from source

### Compile under Linux

For Ubuntu or Debian open a console and install the build tool ant with

```bash
apt-get install ant
```

Download the source code and extract it or you can get the current version by clone the git repository.


```bash
git clone https://github.com/bilderkiste/xmlCAM
```

Now enter the folder where the file build.xml is located ant execute the build tool by typing

```bash
ant
```

You will find the compiled class file in the bin directory.

It is possible to execute with

```bash
java -cp "bin:lib/rsyntaxtextarea-3.6.0.jar:lib/snakeyaml-2.5.jar" main.Main
```

If you want to make a executable jar file type

```bash
ant makejar
```

## A complete program milling a front panel

```xml
<program>
	<tools>
		<tool id="t1" diameter="2"/>
		<tool id="t2" diameter="1.4"/>
	</tools>
	<feedrate>200</feedrate>
	<translate x="10" y="10">
		<!-- Gehäuse -->
		<polyline tool="t1">
			<point x="0" y="2"/>
			<point x="0" y="76"/>
			<spline x="2" y="78"/>
			<point x="76" y="78"/>
			<spline x="78" y="76"/>
			<point x="78" y="2"/>
			<spline x="76" y="0"/>
			<point x="2" y="0"/>
			<spline x="0" y="2"/>
			<depth start="0" end="-4" step="0.4"/>
			<options offset="outset"/>
		</polyline>
		<!-- Display -->
		<translate x="10" y="10">
			<rectangle tool="t1">
				<point x="1.4" y="8.8"/>
				<point x="23.3" y="20.2"/>
				<depth start="0" end="-4" step="0.4"/>
				<options offset="inset"/>
			</rectangle>
			<drill tool="t1">
				<point x="2.25" y="1.85"/>
				<depth start="0" end="-4"/>
			</drill>
			<drill tool="t1">
				<point x="2.25" y="24.85"/>
				<depth start="0" end="-4"/>
			</drill>
			<drill tool="t1">
				<point x="22.75" y="1.85"/>
				<depth start="0" end="-4"/>
			</drill>
			<drill tool="t1">
				<point x="22.75" y="24.85"/>
				<depth start="0" end="-4"/>
			</drill>
		</translate>
		<!-- Taster -->
		<translate x="50" y="15.5">
			<drill tool="t2">
				<point x="0" y="0"/>
				<depth start="0" end="-4"/>
			</drill>
			<drill tool="t2">
				<point x="16" y="0"/>
				<depth start="0" end="-4"/>
			</drill>
			<circle tool="t1">
				<center x="8" y="0"/>
				<radius value="3"/>
				<depth start="0" end="-4" step="0.4"/>
				<options offset="inset"/>
			</circle>
			<drill tool="t2">
				<point x="0" y="16"/>
				<depth start="0" end="-4"/>
			</drill>
			<drill tool="t2">
				<point x="16" y="16"/>
				<depth start="0" end="-4"/>
			</drill>
			<circle tool="t1">
				<center x="8" y="16"/>
				<radius value="3"/>
				<depth start="0" end="-4" step="0.4"/>
				<options offset="inset"/>
			</circle>	
		</translate>
	  	<translate x="12.5" y="45">
	      	<circle tool="t1">
	       		<center x="10" y="10"/>
	              <radius value="10"/>
	  			<depth start="0" end="-4" step="0.4"/>
	  			<options offset="inset"/>
	      	</circle>
	      	<drill tool="t1">
	              <point x="20" y="10"/>
	              <depth start="0" end="-4"/>
	      	</drill>
	   	</translate>
	</translate>
</program>
```