#!/bin/bash
#http://unix.stackexchange.com/questions/11974/resizing-and-croping-images-to-an-aspect-ratio-of-6x4-with-width-of-1024-pixels
# This script resizes only certain images, ie the ones that are words only.
images=("end.png" "s002.png" "s041.png" "s042.png" "s073.png" "s075.png" "s076.png" "s077.png" "s078.png" "s079.png" "s080.png" "s081.png" "s082.png" "s083.png" "s084.png" "s085.png" "s087.png" "s089.png" "s091.png" "s093.png" "s095.png" "s097.png" "s099.png" "s101.png" "s103.png" "s105.png" "s106.png" "s107.png" "s109.png" "s111.png" "s113.png" "s115.png" "s117.png" "s119.png" "s121.png" "s123.png" "s125.png")


for f in "${images[@]}"
do
    echo $f
    convert $f  -gravity center  -crop 400x480+5+0  +repage $f
done
