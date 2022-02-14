
Markdown Editor
===============

Table of contents
-----------------

${toc}

Github Flavored Markdown
------------------------

The `MarkdownEditor` supports Github-Flavored-Markdown ([GFM](https://github.github.com/gfm/)) with some extensions.


### Headings

### Text Styles

|Style          | Syntax        |Example                                     |Output                                |
|---------------|---------------|--------------------------------------------|--------------------------------------|
|Bold           | ** ** or __ _ |\*\*This is bold text\*\*                   |**This is bold text**                 |
|Italic         | * * or _ _    |\*This text is italicized\*                 |*This text is italicized*             |
|Strikethrough  | ~~ ~~         |\~\~This was mistaken text\~\~              |~~This was mistaken text~~            |
|Bold and italic| ** ** and _ _ |\*\*This text is \_extremely\_ important\*\*|**This text is _extremely_ important**|

Extensions
----------

### Formulae

- inline --- write as '$ $': \$V := \frac{3}{4} \pi r^3\$ ==> $V := \frac{4}{3} \pi r^3$.

- as a block --- write as '\$\$ \$\$':

$$
V := \frac{4}{3} \pi r^3
$$

### ==Task Lists==

[x] implemented
[ ] not implemented

### Superscript and Subscript

You can use both ^Super^script and ~Sub~script.

### ==Footnotes==

Here is a footnote reference,[^1] and another.[^longnote]

[^1]: Here is the footnote.

[^longnote]: Here's one with multiple blocks.

    Subsequent paragraphs are indented to show that they belong to the previous footnote.

### Mark

Text can be ==marked== by enclosing in "\=\=".

### ==Common boxes==

::: warning
*here be dragons*
:::

