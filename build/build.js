require('./check-versions')()

process.env.NODE_ENV = 'production'

var ora = require('ora')
var rm = require('rimraf')
var path = require('path')
var chalk = require('chalk')
var webpack = require('webpack')
var config = require('../config')
var webpackConfig = require('./webpack.prod.conf')
// 替换css文件中错误的background-image地址
var rewriteCss = require('./rewrite').rewriteCss

if (process.argv.slice(2)[0] === 'server') {
  var opn = require('opn')
  var express = require('express')
  var app = express()
  var proxyTable = require('../config/proxy')
  var proxyMiddleware = require('http-proxy-middleware')
}

var spinner = ora('building for production...')
spinner.start()

rm(path.join(config.build.assetsRoot, config.build.assetsSubDirectory), err => {
  if (err) throw err
  webpack(webpackConfig, function (err, stats) {
    spinner.stop()
    if (err) throw err
    process.stdout.write(stats.toString({
      colors: true,
      modules: false,
      children: false,
      chunks: false,
      chunkModules: false
    }) + '\n\n')

    console.log(chalk.cyan('  Build complete.\n'))
    console.log(chalk.yellow(
      '  Tip: built files are meant to be served over an HTTP server.\n' +
      '  Opening index.html over file:// won\'t work.\n'
    ))

    rewriteCss()

    if (process.argv.slice(2)[0] === 'server') {
      Object.keys(proxyTable).forEach(function (context) {
        var options = proxyTable[context]
        if (typeof options === 'string') {
          options = { target: options }
        }
        app.use(proxyMiddleware(options.filter || context, options))
      })
      app.use(express.static(path.join(__dirname, '../')))
      app.listen(9999, function () {
        console.log('server start, listening port:9999')
        opn('http://localhost:9999/dist')
      })
    }

  })
})
