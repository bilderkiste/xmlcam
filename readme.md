<p>This element generates G-Code for a line. <br />The line is defined by two points defined with &lt;p&gt; tags. The tupel in &lt;p&gt; defines the x and y position of the point (&lt;p&gt;x,y&lt;/p&gt;).<br />The z-depth must be defined by the &lt;z&gt; tag. The tupel in &lt;z&gt; defines the the start layer (workpiece surface), the end layer (depth), and the steps (&lt;z&gt;startZ,endZ,stepZ&lt;/z&gt;).</p>
<pre>&lt;line&gt;<br />  &lt;p&gt;40,200&lt;/p&gt;<br />  &lt;p&gt;340,250&lt;/p&gt;<br />  &lt;z&gt;0,-1,0.1&lt;/z&gt;<br />&lt;/line&gt;</pre>
<p>This code snippet has the following result:</p>
<p><img src="images/elements/lineelement.png" alt="A line" /></p>
