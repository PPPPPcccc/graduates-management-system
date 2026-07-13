import axios from 'axios'

const request = axios.create({
  baseURL: '/api',
  timeout: 10000
})

// 请求拦截器：undefined → null，确保后端能收到所有字段
request.interceptors.request.use(config => {
  config.data = convertUndefinedToNull(config.data)
  return config
})

function convertUndefinedToNull(obj) {
  if (obj === null || obj === undefined) return null
  if (Array.isArray(obj)) return obj.map(convertUndefinedToNull)
  if (typeof obj === 'object') {
    const result = {}
    for (const key of Object.keys(obj)) {
      result[key] = convertUndefinedToNull(obj[key])
    }
    return result
  }
  return obj
}

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
