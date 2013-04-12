MAIN_FILE=proj2_tasks.pdf
MAIN_NAME=$(MAIN_FILE:.pdf=)

COMMENTED_MAIN_FILE=$(MAIN_NAME)_commented.pdf

FIG_FILES=$(wildcard ../figures/*.pdf)
SRC_FILES=$(wildcard src/*.tex)

COMMENT_FILE=comment.cut
REFERENCE_FILE=bib/references.bib
RELEASE_MAIN_FILE=../pdf/$(MAIN_NAME)_latest.pdf
RELEASE_COMMENTED_FILE=../pdf/$(COMMENTED_MAIN_FILE)

TRASH_FILES:=$(MAIN_NAME).aux
TRASH_FILES+=$(MAIN_NAME).bbl
TRASH_FILES+=$(MAIN_NAME).out
TRASH_FILES+=$(MAIN_NAME)-blx.bib
TRASH_FILES+=$(MAIN_NAME).run.xml
TRASH_FILES+=$(MAIN_NAME).blg
TRASH_FILES+=$(MAIN_NAME).log
TRASH_FILES+=$(MAIN_NAME).pdf
TRASH_FILES+=$(MAIN_NAME).ps
TRASH_FILES+=$(MAIN_NAME).dvi
TRASH_FILES+=$(COMMENT_FILE)
TRASH_FILES+=$(COMMENTED_MAIN_FILE)

PDFLATEX=pdflatex
LATEX=latex
BIBTEX=bibtex

MAIN_OPTIONS=$(MAIN_NAME)
COMMENTED_OPTIONS="\def\usecomments{}\input{$(MAIN_NAME)}"

.SECONDARY: $(MAIN_NAME).bbl
.PHONY: default clean preview

default: build

build: $(MAIN_FILE)

clean:
	-rm -f $(TRASH_FILES)

rebuild:
	@$(MAKE) clean
	@$(MAKE) build

release:
	@$(MAKE) rebuild
	cp -f $(MAIN_FILE) $(RELEASE_MAIN_FILE)
	#cp -f $(COMMENTED_MAIN_FILE) $(RELEASE_COMMENTED_FILE)

%.pdf: %.tex $(FIG_FILES) $(SRC_FILES) $(REFERENCE_FILE)
	@$(MAKE) $(MAIN_NAME).bbl
#	@$(PDFLATEX) $(COMMENTED_OPTIONS)
#	@$(PDFLATEX) $(COMMENTED_OPTIONS)
#	@cp -f $(MAIN_FILE) $(COMMENTED_MAIN_FILE)
	@$(PDFLATEX) $(MAIN_OPTIONS)
	@$(PDFLATEX) $(MAIN_OPTIONS)
	@-rm -f $(COMMENT_FILE)

%.bbl: $(REFERENCE_FILE)
	@$(PDFLATEX) $(COMMENTED_OPTIONS)
	@$(BIBTEX) $(MAIN_NAME)
	@$(PDFLATEX) $(COMMENTED_OPTIONS)

build-paper-only:
	@$(PDFLATEX) $(MAIN_OPTIONS)
	@$(PDFLATEX) $(MAIN_OPTIONS)
	@-rm -f $(COMMENT_FILE)

ps: $(MAIN_NAME).tex $(FIG_FILES) $(SRC_FILES) $(REFERENCE_FILE)
	@$(MAKE) $(MAIN_NAME).bbl
	@$(LATEX) $(MAIN_OPTIONS)
	@$(LATEX) $(MAIN_OPTIONS)
	dvips $(MAIN_NAME).dvi -o $(MAIN_NAME).ps
	@-rm -f $(COMMENT_FILE)

#Following target only works on OSX
preview: build
	open -a preview $(MAIN_FILE)

