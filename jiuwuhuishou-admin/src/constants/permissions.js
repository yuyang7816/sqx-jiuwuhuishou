/**
 * 管理端按钮权限码（与 sys_menu.type=2 的 perms 对齐）
 * 注意：库中个别拼写保留原样（如 pickupTast、allication、shenhe）
 */
export const PERMS = {
  // 用户中心
  userList: 'userList:list',
  userUpdate: 'userList:update',
  userUpdateStatus: 'userList:updateStatus',
  userUpdateAmount: 'userList:updateJe',
  userUpdateCoin: 'userList:updateJf',
  userDelete: 'userList:delete',
  userUpdateCommission: 'userList:updateFxsy',
  userAddressList: 'userAddress:list',

  // 订单
  orderList: 'orderList:list',
  orderCancel: 'orderList:cancel',
  orderAssign: 'orderList:allocation',
  orderRescheduleList: 'orderReschedule:list',
  orderExceptionList: 'orderException:list',

  // 取件
  pickupList: 'pickupTast:list',
  pickupAssign: 'pickupTask:allication',
  pickupComplete: 'pickupTask:complete',

  // 履约
  settlementList: 'settlement:list',
  payoutList: 'payout:list',
  disputeList: 'dispute:list',
  reviewList: 'review:list',

  // 回收配置
  recycleCategoryList: 'recycleCategory:list',
  recycleCategoryAdd: 'recycleCategory:add',
  recycleCategoryUpdate: 'recycleCategory:update',
  recycleCategoryDelete: 'recycleCategory:delete',
  serviceAreaAdd: 'serviceArea:add',
  serviceAreaUpdate: 'serviceArea:update',
  serviceAreaDelete: 'serviceArea:delete',
  recycleRuleAdd: 'recycleRule:add',
  recycleRuleUpdate: 'recycleRule:update',
  recycleRuleDelete: 'recycleRule:delete',
  productCategoryAdd: 'productCategory:add',
  productCategoryUpdate: 'productCategory:update',
  productCategoryDelete: 'productCategory:delete',
  bookWeightAdd: 'bookWeightOption:add',
  bookWeightUpdate: 'bookWeightOption:update',

  // 财务
  userWalletUpdate: 'userWallet:update',
  withdrawAudit: 'withdrawRecord:shenhe',
  referralUserCommissionList: 'referralUserCommission:list',

  // 运营
  bannerAdd: 'bannerList:add',
  bannerUpdate: 'bannerList:update',
  bannerDelete: 'bannerList:delete',
  messageList: 'message:list',
  helpAdd: 'helpCategory:add',
  helpUpdate: 'helpCategory:update',
  helpDelete: 'helpCategory:delete',
  brandFollow: 'brandCooperation:shenhe',
  welfareAdd: 'welfareTask:add',
  welfareUpdate: 'welfareTask:update',
  welfareDelete: 'welfareTask:delete',
  userWelfareList: 'userWelfare:list',

  // 客服
  serviceChatSend: 'serviceChat:fasong',
}
