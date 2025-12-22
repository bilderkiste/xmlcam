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

```xml
<tools>
  <tool id="t1" type="ballend" diameter="1.2"/>
  <tool id="t2" type="endmill" diameter="2.5"/>
</tools>
```

---

## Settings

```yaml
dialect: GRBL
security-height: 5
workbench: [0, 0, 400, 400]
grid-step: 50
font-size: 18
standard-dir: /home/test/xmlCAM
```

---

## Dialects

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