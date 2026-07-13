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
export function addGraduate(data) {
  return request.post('/graduate', data)
}
export function batchAddGraduates(rows) {
  return request.post('/graduate/batch', rows)
}
export function deleteGraduate(id) {
  return request.delete(`/graduate/${id}`)
}
export function batchDeleteGraduates(ids) {
  return request.post('/graduate/batch-delete', ids)
}