const path = require('path');
const {CleanWebpackPlugin} = require('clean-webpack-plugin');
const HtmlWebpackPlugin = require('html-webpack-plugin');

module.exports = {
    entry: {
        "editor": './src/editor.js',
        "editor.worker": 'monaco-editor/esm/vs/editor/editor.worker.js',
        "json.worker": 'monaco-editor/esm/vs/language/json/json.worker',
        "css.worker": 'monaco-editor/esm/vs/language/css/css.worker',
        "html.worker": 'monaco-editor/esm/vs/language/html/html.worker',
        "ts.worker": 'monaco-editor/esm/vs/language/typescript/ts.worker',
    },
    output: {
        globalObject: 'self',
        filename: '[name].bundle.js',
        path: path.resolve(__dirname, 'dist')
    },
    optimization: {
        minimize: false
    },
    module: {
        rules: [{
            test: /\.css$/,
            use: ['style-loader', 'css-loader']
        }]
    },
    plugins: [
        new CleanWebpackPlugin(),
        new HtmlWebpackPlugin({
            filename: 'editor_text.html',
            template: 'src/editor_text.html'
        }),
        new HtmlWebpackPlugin({
            filename: 'editor_markdown.html',
            template: 'src/editor_markdown.html'
        })
    ]
};
