import CodeMirror from 'codemirror';

import 'codemirror/mode/meta.js';
import 'codemirror/addon/selection/active-line.js';
import 'codemirror/addon/scroll/simplescrollbars.js';
import 'codemirror/addon/search/searchcursor.js';
import 'codemirror/addon/search/search.js';
import 'codemirror/addon/mode/loadmode.js';
import 'codemirror/addon/display/fullscreen.js';
import 'codemirror/addon/display/placeholder.js';

import 'codemirror/lib/codemirror.css';
import 'codemirror/theme/xq-dark.css';
import 'codemirror/theme/xq-light.css';
import 'codemirror/addon/display/fullscreen.css';
import 'codemirror/addon/scroll/simplescrollbars.css';
import 'codemirror/addon/search/matchesonscrollbar.css';

CodeMirror.modeURL = 'codemirror/mode/%N/%N.js';

export const code_editor = CodeMirror.fromTextArea(document.getElementById('code_editor'), {
    fullScreen : true,
    scrollbarStyle : 'overlay',
    mode : 'text',
    lineNumbers : true,
    inputStyle : 'textarea'
});
