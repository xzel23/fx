Supported Markdown syntax
=========================

Basic Formatting
----------------

 markdown         | alternative           | rendered as        |
------------------|-----------------------|--------------------|
| `__bold__`      | `**bold**`            | **bold**           |
| `*italic*`      | `_italic_`            | *italic*           |
| `~~strike~~`    |                       | ~~strike~~         |
| `__*combined*__`|                       | __*combined*__     |

Headings
--------

 markdown           | alternative                | rendered as        |
--------------------|----------------------------|--------------------|
 `# Heading 1`      | `Heading 1`<br>`=========` | <h1>Heading 1</h1> |
 `## Heading 2`     | `Heading 2`<br>`---------` | <h2>Heading 2</h2> |
 `### Heading 3`    |                            | <h3>Heading 3</h3> |
 `#### Heading 4`   |                            | <h4>Heading 4</h4> |
 `##### Heading 5`  |                            | <h5>Heading 5</h5> |
 `###### Heading 6` |                            | <h6>Heading 6</h6> |

Lists
-----

### unordered Lists

```
 - item 1

   indented text must come after an empty line

   paragraphs are separated by empty lines

 - item 2
```
and
```
 * item 1

   indented text must come after an empty line

   paragraphs are separated by empty lines

 * item 2
```

both render as:

 - item 1

   indented text must come after an empty line

   paragraphs are separated by empty lines

 - item 2

### ordered Lists

```
 1. item 1

    again an empty line must be inserted
    before indented text

    second indented paragraph

 2. item 2
```
 1. item 1

    again an empty line must be inserted
    before indented text

    second indented paragraph

 2. item 2

Links
-----

```
Markdown description on [Wikipedia](en.wikipedia.org).
```
Markdown description on [Wikipedia](en.wikipedia.org).
```

Markdown description on [Wikipedia][1].

[1]: http://www.en.wikipedia.org
```
Markdown description on [Wikipedia][1].

[1]: http://www.en.wikipedia.org

Math
----

```
Inline Mathematical expressions in $\LaTeX$ syntax like $f(x)=x^2$ are ecclosed in '$'.
```
Inline Mathematical expressions in $\LaTeX$ syntax like $f(x)=x^2$ are ecclosed in '$'.

Math blocks are enclosed in `$$`:
```
$$ V = \frac{4}{3} \pi r^3 $$
```
$$ V = \frac{4}{3} \pi r^3 $$
