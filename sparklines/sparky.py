#!/usr/bin/env python
# encoding: utf-8
"""
A simple chart plotting library designed to create SparkLine-like charts. Each
graphic is called a "spark." Sparks are created in the context of a figure, and
a figure can have one or more sparks.

A figure can have a specific size, with sparks scaled to fit, or sparks can have 
a size with the figure scaled to contain them.

A spark has horizontal and vertical axes, which can be visible or invisible.
Data can be plotted on, above, or below the axis.

Sparks have several attributes which can be set on a figure-wide basis and
overridden on a per-spark basis.

Data is specified as an array of numeric values. Arrays of arbitrary values
can also be used as long as a conversion function is provided. Multiple data
sets can be plotted on a single spark.

Several built-in plotting functions are provided, but user-defined plots can
be created as well.

This module can be used as a library, or can be executed in several ways:
* No arguments: read a comma-separated list of data points from stdin
* -d: plot one or more comma-separated data sets using default values
* -f: read data sets from a csv file (each line creates a separate Spark)
* -r: create a plot with random data

Spark attribute values can be passed in with the -a option.
"""

import sys
import os

# program version
VERSION = 0.1

# orientations
VERTICAL = 0
HORIZONTAL = 1

# visibility
INVISIBLE = 0
VISIBLE = 1
POSITIVE = 2
NEGATIVE = 4

class Figure(object):
    '''
    A figure that can contain one or more Sparks. Any Spark attribute may be set 
    on a Figure, in which case it will be used as the default for all Sparks.
    '''
    def __init__(self, size=None, margins=None, spacing=20, orientation=VERTICAL,\
                 layout=1, rendering="Agg"):
        '''
        Create a Figure.
        
        :size:
            Size of the figure in pixels. Either a single number, which specifies
            a square figure, or a tuple (width,height).
        :margins:
            Amount of whitespace left around the edges in pixels. Either a single
            number, which specifies uniform margins, or a tuple 
            (vertical,horizontal) or (top,right,bottom,left).
        :spacing:
            Space between each spark in pixels. Defaults to 20.
        :orientation:
            Whether each spark should be placed below (VERTICAL) or to the
            right (HORIZONTAL) of the previous one.
        :layout:
            The number of rows/columns. Can be a single number, which specifies
            the number of columns if orientation is VERTICAL or rows if 
            orientation is HORIZONTAL, or a tuple (rows,columns).
        :rendering:
            The rendering method; this can be any rendering method supported by
            matplotlib. Default is 'Agg', which produces PNGs.
        '''
        self.size = size
        self.margins = margins
        self.spacing = spacing
        self.orientation = orientation
        self.layout = layout
        self._sparks = []
        
    def add_spark(spark=None):
        '''
        Add a new Spark to this figure. If ``spark`` is none, a new Spark is
        created with default attribute values and returned.
        '''
        if spark is None: spark = Spark(self)
        self._sparks.append(spark)
        return spark
        
    def plot(file=None):
        '''
        Create the figure and save it to the specified file. If no file is
        specified, a window is opened to display the figure.
        '''
        pass

class Spark(object):
    '''
    A SparkLine-like graphic. There are many attributes that can be used for
    customization:
    
    :title:
        Text title of the spark.
    :size:
        Tuple (width,height) specifying the size of the spark in pixels.
    :aspect:
        If sparks are being auto-sized, you can still specify the aspect
        ratio. This is a number in the range (0,1) that specifies the
        width as a percentage of the height.
    :axis_color:
        The default axis color.
    :axis_width:
        The default width of axis lines.
    :h_axis:
        How to draw the horizontal axis. Multiple values are accepted:
        * VISIBLE/INVISIBLE
        * POSITIVE/NEGATIVE/BOTH: draw the axis for positive values,
          negative values, or both
        * An Axis object, which provides fine-grain control over the drawing
          of an axis.
    :v_axis:
        Same as h_axis, but for the vertical axis.
    :line_width:
        The default line width for drawing all parts of the spark.
    :line_color:
        The default line color for drawing all parts of the spark.
    :gap_size:
        The number of pixels to leave between adjacent data points.
    :plot:
        The plot type or array of plot types. Each plot type can be the string 
        name of one of the built-in plot types, or it can be a Plot object, 
        which allows fine-grain control over plotting.
    :data:
        The array of data points to plot. If plot is an array, then data must
        be a two-dimensional array with the same number of rows as there are
        plots.
    '''
    def __init__(self):
        pass

class Axis(object):
    pass
    
class Plot(object):
    pass

if __name__ == '__main__':
    def add_opts(parser):
        parser.add_option("-d", "--data", type="string", metavar="DATA", 
            help="Comma-delimited data set.")
        parser.add_option("-f", "--data-file", type="string", metavar="FILE",
            action="callback", callback=validator.readable_file,
            help="File containing comma-delimited data.")
        parser.add_option("-r", "--random-data", 
            help="Create a spark with random data.")
        parser.add_option("-o", "--output-file", type="string" metavar="FILE",
            default="figure.png" action="callback", 
            callback=validator.writable_file, help="Output file for figure.")
        parser.add_option("-F", "--image-format", type="string" metavar="FORMAT",
            help="Image output format; default is Agg.")
    
    # parse commandline arguments
    (options, args) = parse_options(argv or sys.argv, VERSION, add_opts)
    
    