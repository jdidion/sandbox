#!/usr/bin/env python
# Implementation of a pipeline for compiling a PDF document
# from a mixture of markdown, R-markdown and latex. The project
# should be contained in a directory with three subdirectories:
# latex - contains a template (.tex extension) and modules (.sty)
# sections - contains markdown (.md) and R-markdown (.Rmd) files
# bib - contains biblographic library(ies)

import os
import shutil
import sys

sys.path.insert(0, os.path.join(os.environ['SCI_HOME'], "common", "lib"))
from util.cl import parse
from util.misc import bash
    
def compile_pdf(latex_dir, section_dir, bib_dir, outfile, working_dir=None, cleanup=False,
        pandoc="pandoc", pdflatex="pdflatex"):
    if working_dir is None:
        from tempfile import mkdtemp
        working_dir = mkdtemp()
        print "Working dir: {0}".format(working_dir)
        cleanup = True
    elif not os.path.exists(working_dir):
        os.makedirs(working_dir)
    
    # copy latex files and identify main .tex file
    main_tex = None
    for fname in os.listdir(latex_dir):
        dest = os.path.join(working_dir, fname)
        shutil.copy(os.path.join(latex_dir, fname), dest)
        if os.path.splitext(fname)[1] == "tex":
            assert main_tex is None, "More than one .tex file"
            main_tex = dest
    
    # convert md and Rmd files to .tex files
    for fname in os.listdir(section_dir):
        base, ext = os.path.splitext(fname)
        src = os.path.join(section_dir, fname)
        md_dest = os.path.join(working_dir, "{0}.md".format(base))
        tex_dest = os.path.join(working_dir, "{0}.tex".format(base))
        if ext == ".md":
            shutil.copy(src, md_dest)
        elif ext == ".Rmd":
            rcmd = 'Rscript -e "require(knitr); require(markdown); setwd({0}); knit({1},{2})"'.format(working_dir, src, md_dest)
            bash(rcmd, catch=False)
        else:
            raise Exception("Unrecognized extension: {0}".format(ext))
        
        pandoc_cmd = "{0} -f markdown -t latex {1} -o {2}".format(pandoc, md_dest, tex_dest)
        bash(pandoc_cmd, catch=False)
    
    for fname in os.listdir(bib_dir):
        shutil.copy(os.path.join(bib_dir, fname), os.path.join(working_dir, fname))
    
    # run pdf2latex
    pdf_cmd = "{0} {1}".format(pdflatex, main_tex)
    bash(pdf_cmd, catch=False, cwd=working_dir)
    
    # move pdf file to outfile
    pdf_file = os.path.join(working_dir, "{0}.pdf".format(os.path.splitext(main_tex)[0]))
    shutil.move(pdf_file, outfile)
    
    if cleanup:
        shutil.rmtree(working_dir)
    
    return pdf_file

def main():
    def parse_args(parser):
        parser.add_argument("-c", "--cleanup", action="store_true", default=False)
        parser.add_argument("-w", "--working_dir", type="writeable_dir", default=None)
        parser.add_argument("--pandoc_exe", default="pandoc")
        parser.add_argument("--pdflatex_exe", default="pdflatex")
        parser.add_argument("--open_pdf", action="store_true", default=False)
        parser.add_argument("dir", type="readable_dir")
        parser.add_argument("outfile", type="writeable_file")
    
    args = parse(parse_args)
    
    latex_dir = os.path.join(args.dir, "latex")
    assert os.path.exists(latex_dir), "Missing directory: {0}".format(latex_dir)
    
    section_dir = os.path.join(args.dir, "sections")
    assert os.path.exists(section_dir), "Missing directory: {0}".format(section_dir)
    
    bib_dir = os.path.join(args.dir, "bib")
    assert os.path.exists(bib_dir), "Missing directory: {0}".format(bib_dir)
    
    pdf = compile_pdf(latex_dir, section_dir, bib_dir, args.outfile, args.working_dir, 
        args.cleanup, args.pandoc_exe, args.pdflatex_exe)
    
    if args.open_pdf:
        bash("open {0} &".format(pdf), catch=False)

if __name__ == "__main__":
    main()