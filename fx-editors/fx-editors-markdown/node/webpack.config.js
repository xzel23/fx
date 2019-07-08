const path = require('path');

module.exports = {
    entry: './src/markdown_editor.js',
    output: {
        path: path.resolve(__dirname, 'dist'),
        filename: 'markdown_editor.bundle.js'
    },
    module: {
        rules: [
            { test: /\.css$/, use: ['style-loader', 'css-loader'] }
        ]
    },
    optimization: {
        minimize: true
    }
};
