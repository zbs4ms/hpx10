module.exports =  {
  '/reservation': {
    target: 'http://doc.hpx10.com:50002/',
    changeOrigin: true,
    // pathRewrite: {
    //   '^/admin': '/admin'
    // },
    // https
    secure: false
  }
}


