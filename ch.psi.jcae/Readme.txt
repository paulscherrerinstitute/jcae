Readme
------

Google Code
-----------
Use maven goal gcupload:gcupload to upload to google code.

Notes
-----

Channel Access "Specification": http://epics.cosylab.com/cosyjava/JCA-Common/Documentation/CAproto.html

DBR - data request buffer types
DBF - database field types

Values can be set in different ways:
normal - wait until channel returns acknowledgement
async - get a handle (i.e. Future) to wait for set operation to finish
noWait - issues a set command while requesting no acknowledgement (fire and forget)