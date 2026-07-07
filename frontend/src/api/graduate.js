// api/graduate.js
import request from './request'
export function fetchGraduateList(params) {
  return request.get('/graduate/list', { params })
}
export function updateGraduate(id, data) {
  return request.put(`/graduate/${id}`, data)
}
export function fetchFilterOptions() {
  return request.get('/graduate/filter-options')
}