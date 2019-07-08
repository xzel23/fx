const path = require('path');

module.exports = {
    entry: './src/editor.js',
    output: {
        path: path.resolve(__dirname, 'dist'),
        filename: 'editor.js'
    },
    module: {
        rules: [
            { test: /\.css$/, use: ['style-loader', 'css-loader'] },
            {
                test: /\.(png|jp(e*)g|svg)$/,
                use: [{
                    loader: 'file-loader',
                    options: {
                        name: '[name].[ext]?[hash]',
                        //outputPath: 'img/'
                    }
                }]
            }
        ]
    },
    optimization: {
        minimize: true
    }
};
