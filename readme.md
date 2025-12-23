# This is the documentation site for xmlCAM.

## What is xmlCAM?

xmlCAM is a software application designed to facilitate the rapid and efficient generation of G-code for CNC milling machines. Toolpaths are defined exclusively through XML elements, enabling a structured and flexible workflow.

Please note that xmlCAM is in an early stage of development and is provided without any warranty of any kind. Although I intend to continue its development, programming is not my primary profession, and all work on the project is carried out in my spare time. I therefore kindly ask for your understanding should you encounter software bugs.

xmlCAM is distributed as free software under the **GNU General Public License, version 3**, as published by the Free Software Foundation.

All measurements within the software adhere to the metric system. Length values are expressed in millimeters **(mm)**, and velocities in millimeters per second **(mm/s)**, unless explicitly stated otherwise.

The software is developed and tested using GRBL v1.1 firmware. Other firmware implementations may interpret G-code differently. In such cases, please consult the Dialects section.

I am **not** responsible for any damage that may occur to your machine. Please exercise caution when using this software in its current early-development state, as the reliability of the generated G-code is still being evaluated.

The XML syntax has been updated since version 0.100. For information on the previous syntax, please refer to the readme.md included with that release.

## Views

### XML View

The XML View displays the XML document that serves as the source for G-code generation. You may edit the XML within the text pane and generate the corresponding G-code by selecting “Generate G-Code.”

A real-time validator is provided to check the XML for correctness. Errors are indicated by red markers and a description of each error is shown in the field above the XML text pane.

### Table View

The Table View displays the generated G-code. You can edit a cell by clicking on it. If the value you enter is invalid, your input will be discarded.

Selecting “Insert Row” will insert a new row above the currently selected row. Selecting “Delete Row” will remove the selected row or, in the case of multiple selection, all selected rows. By clicking “New Field,” a new field will be inserted into the selected row.

### Graphic View

The Graphic View displays the generated G-code visually, as its name suggests. The ruler at the bottom indicates the x-axis, and the ruler on the left indicates the y-axis.

Blue lines represent the original shapes as defined in the XML.

Green lines represent G0 movements at the configured safety height for the z-axis. This safety height can be specified in the settings.txt file; additional information regarding these settings is provided below.

Black lines represent G1 movements, which are executed at the z height defined in the <z> tag.

You can zoom in and out using the “+” and “–” buttons.

In the menu under Graphic View, you can show or hide G0 and G1 movements, the calculated points, and the grid that corresponds to the x and y rulers.

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