import axios from 'axios'

const request = axios.create({
  baseURL: '/api',
  timeout: 10000
})

export function login(username, password) {
  return request.post('/auth/login', { username, password })
}

export function logout() {
  return request.post('/auth/logout')
}

export function getCurrentUser() {
  return request.get('/auth/me')
}