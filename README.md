# HWTeXGenerator
Generates a TeX template by parsing CSE 311/312/446 homework assignments. 
1. Clone or download.
2. Run HWTeXGen.jar
3. Select class from the dropdown menu.
4. Browse the homework pdf e.g. hw0.pdf
5. Generate.

TODO: Make it less ugly, auto-detect, add custom classes permanently, drag and drop for input file, instructions in pop-out.

![GUI image](https://github.com/NelsonTanCS/HWTeXGenerator/blob/master/texgen.PNG)

Custom: To use custom, choose "Custom" from the dropdown menu. In the first text box next to "Custom:" type the format of the lists to detect and in the second text box type the format of the sublists. For example, if the questions are numbered "(1), (2), (3)" type "(#)" in the first box and if the parts of the questions are lettered "a., b., c." type "\*." in the second box.

Manual: To use manual select the bubble next to "Manual" and type the number of parts per question in a comma-separated list without spaces. For example, if there are 3 questions with the first question having 3 parts and the other two questions having 2 parts then type "3,2,2" in the textbox. You only have to choose a save location for this option.
