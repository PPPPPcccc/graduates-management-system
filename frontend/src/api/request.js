import axios from 'axios'

const request = axios.create({
  baseURL: '/api',
  timeout: 10000
})

// 响应拦截器：统一处理未登录等
request.interceptors.response.use(
  response => response,
  error => {
    if (error.response?.status === 401) {
      localStorage.clear()
      window.location.href = '/login'
    }
    return Promise.reject(error)
  }
)

export default request
