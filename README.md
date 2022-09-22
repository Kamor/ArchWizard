# ArchWizard
Tool for an easier access of the arch data. It is the next step from my archdiag idea.
This time it's coded in java to have a crossplatform for my code.

Tool first read in all data, so have patience. Tool is not hanging, it's reading or writing data.
Then you can browse the data by text or by table.
It has a search field to search for attributes.
Search field works like a kind of filter to the data.
It has also a second filter, to reduce the amount of data and to focus on special attributes.
Tool has a small edit function for the tablemode, it can also overwrite full columns with same data.
Tool can export to csv format. Tool can import from csv format. Currently it needs a ; for separator.
Tool has also a small backup function for the data.

Carefully it's still version 0.0. So backup your data first.
It also touch a lot of files, so other programms like SVN could be irritated if you change, restore, import to much.

Tool cannot generate new archfiles, it can only read from and write to existing files.

Tool needs java runtime.
Start tool with -> java archwizard.java
On first start tool need the path to the archfolder.
You can use tool function config-> setpath or write path manuelly in archwizard.ini.

For more information use the help function or read the archwizard.txt
For example
path D:\_daimonin\test
