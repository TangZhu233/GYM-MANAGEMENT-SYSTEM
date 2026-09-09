// 员工职务：唯一候选来源，列表筛选与新增/编辑表单共用，避免两处名单漂移漏项。
export const STAFF_OPTIONS = [
  { label: '健身教练', value: '健身教练' },
  { label: '保洁员', value: '保洁员' },
  { label: '前台', value: '前台' },
  { label: '经理', value: '经理' }
]

// 职务在列表里的彩色小标签：教练=蓝、经理=红、前台=橙、保洁=灰，未收录的自定义职务用中性灰兜底。
export function staffTagType(staff) {
  const map = {
    '健身教练': 'primary',
    '经理': 'danger',
    '前台': 'warning',
    '保洁员': 'info'
  }
  return map[staff] || 'info'
}
