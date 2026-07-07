// api/account.js
import request from './request'
export function fetchAccountsWithPlain() {
  return request.get('/account/list')
}
export function getAccountPlain(id) {
  return request.get(`/account/${id}/plain`)
}