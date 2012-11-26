#!/bin/bash
#http://unix.stackexchange.com/questions/11974/resizing-and-croping-images-to-an-aspect-ratio-of-6x4-with-width-of-1024-pixels
for f in *.png
do
    echo $f
    convert $f  -gravity center  -crop 380x430+5+0  +repage $f
done
