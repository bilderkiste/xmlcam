G90 ; Absolute positioning, just in case
G21; Set units to mm
G92 X0 Y0 Z0 ; Set Current position to 0, all axes
G00 Z6.0000 F500 ; Raise Z 5mm at 8.3mm/s to clear clamps and screws
M03 S24000 ; PID, set spindle speed
