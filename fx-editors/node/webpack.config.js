const path = require('path');

module.exports = {
    entry: './src/code_editor.js',
    output: {
        path: path.resolve(__dirname, 'dist'),
        filename: 'code_editor.bundle.js'
    },
    module: {
        rules: [
            { test: /\.css$/, use: ['style-loader', 'css-loader'] }
        ]
    },
    optimization: {
        minimize: false
    }
};
