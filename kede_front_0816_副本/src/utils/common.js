// 避免空串
function my_trim(val) {
  if (!val) return "";
  return val;
}
// 展示字符串，超过指定长度截取 val,len
function strShow(val, len) {
  if (!val) return "";
  if (val.length >= len) return val.substring(0, len + 1);
  return val;
}

// 多个字符串合并
function my_merge(...vals) {
  let str = "";
  for (let i = 0; i < vals.length; i++) {
    if (!vals[i]) continue;
    str += vals[i];
    if (i != vals.length - 1) str += "/";
  }
  // 去掉结尾多余 "/"
  if (str.endsWith("/")) {
    str = str.substring(0, str.lastIndexOf("/"));
  }
  // 去掉中间多余 “/”
  str = replaceSplit(str, "/");
  return str;
}
// 多个对象属性合并  参数([obj, "propertyName"])
function my_obj_merge(...vals) {
  let str = "";
  for (let i = 0; i < vals.length; i++) {
    if (!vals[i][0] || !vals[i][0][vals[i][1]]) continue;
    str += vals[i][0][vals[i][1]];
    if (i != vals.length - 1) str += "/";
  }
  // 去掉结尾多余 "/"
  if (str.endsWith("/")) {
    str = str.substring(0, str.lastIndexOf("/"));
  }
  // 去掉中间多余 “/”
  str = replaceSplit(str, "/");
  return str;
}

function replaceSplit(str, split) {
  let split2 = split + split;
  while (str.indexOf(split2) != -1) {
    str = str.replace(split2, split);
  }
  return str;
}
function myReplaceMany(str, arr) {
  arr.forEach((item) => {
    while (str.indexOf(item) != -1) {
      str = str.replace(item, "");
    }
  });
  return str;
}

// 字符串是否为空
function isNull(val) {
  if (!val) return true;
  let temp = val;
  if (typeof temp != "string") temp += "";
  if (temp.trim() == "") return true;
  return false;
}

// 值或者对象中数字是否存在 'NaN'，如果存在将NaN转换为空,返回一个正确对象
function transferNaNToEmpty(val) {
  if (!val || !(val instanceof Object)) return val;
  let tempStr = JSON.stringify(val);
  if (tempStr) {
    let tempObj = JSON.parse(tempStr.replaceAll("NaN", ""));
    return tempObj;
  }
  return val;
}

import BigNumber from "bignumber.js";
// p1,p2   type: 0:加;1:减;2:乘;3:除;
function computer(p1, p2, type, fixed = 2) {
  let a = new BigNumber(p1 + "");
  let b = new BigNumber(p2 + "");

  if (type == 0) {
    return a.plus(b).toFixed(fixed);
  } else if (type == 1) {
    return a.minus(b).toFixed(fixed);
  } else if (type == 2) {
    return a.multipliedBy(b).toFixed(fixed);
  } else if (type == 3) {
    if (b == 0) b = new BigNumber(1 + "");
    return a.div(b).toFixed(fixed);
  }
  return 0;
}

// 时间戳：1637244864707
/* 
    时间戳转换为时间 
    type: 1:yyyy-MM-dd HH:mm:ss;   2:yyyy-MM-dd;
*/
function timestampToTime(timestamp, type) {
  timestamp = timestamp ? timestamp : null;
  let date = new Date(timestamp); //时间戳为10位需*1000，时间戳为13位的话不需乘1000
  let Y = date.getFullYear() + "-";
  let M =
    (date.getMonth() + 1 < 10
      ? "0" + (date.getMonth() + 1)
      : date.getMonth() + 1) + "-";
  let D = (date.getDate() < 10 ? "0" + date.getDate() : date.getDate()) + " ";
  let h =
    (date.getHours() < 10 ? "0" + date.getHours() : date.getHours()) + ":";
  let m =
    (date.getMinutes() < 10 ? "0" + date.getMinutes() : date.getMinutes()) +
    ":";
  let s = date.getSeconds() < 10 ? "0" + date.getSeconds() : date.getSeconds();

  if (type == 1) return Y + M + D + h + m + s;
  else if (type == 2) return Y + M + D;
}

// 商品计算小计
function my_calFinalPrice(index, params, hasFreight) {
  let finalPrice = BigNumber(0);
  if (params && params.length > 0) {
    if (params[index].totalPrice) {
      finalPrice = finalPrice.plus(BigNumber(params[index].totalPrice));
    }
    if (params[index].freight && hasFreight == 0) {
      finalPrice = finalPrice.plus(BigNumber(params[index].freight));
    }
    if (params[index].markupAmount) {
      finalPrice = finalPrice.plus(BigNumber(params[index].markupAmount));
    }
    if (params[index].customerMarkup) {
      finalPrice = finalPrice.plus(BigNumber(params[index].customerMarkup));
    }
    if (params[index].salesMarkup) {
      finalPrice = finalPrice.plus(BigNumber(params[index].salesMarkup));
    }
    if (params[index].priceReductionAmount) {
      finalPrice = finalPrice.minus(
        BigNumber(params[index].priceReductionAmount)
      );
    }
    if (params[index].otherServiceFees) {
      finalPrice = finalPrice.plus(BigNumber(params[index].otherServiceFees));
    }
  }
  // console.log("index=======111===========>"+finalPrice);
  return finalPrice;
}

/**
 * 获取值，谁不为空 返回谁
 * @param  {...any} params 数组
 * @returns
 */
function getA_or_B(params) {
  for (let i = 0; i < params.length; i++) {
    // console.log("=========>"+params[i])
    if (params[i] && !isNull(params[i])) {
      return params[i];
    }
  }
  return "";
}

function colorByStatus(orderStatus = 0) {
  // 00   暂存   #D1DDDB
  // 1   未审核(销售刚提交)   #85B8CB
  // 10   已审核(销售负责人审核)   #1D6A96
  // 20   采购中(后台采购中)   #283B42
  // 30   已入库(后台入库后)   #7BC5AE
  // 40   已出库(后台出库后)   #028C6A
  // 60   已领取(待送货)   #003E19
  // 80   已送货(待回款)   #7FB174
  // 100   已回款   #072A24
  // 110   已退货   #E81E25
  // console.log("orderStatus=======>"+typeof orderStatus)
  let color = "#D1DDDB";
  switch (orderStatus) {
    case "1":
      color = "#85B8CB";
      break;
    case "10":
      color = "#1D6A96";
      break;
    case "20":
      color = "#283B42";
      break;
    case "30":
      color = "#7BC5AE";
      break;
    case "40":
      color = "#028C6A";
      break;
    case "60":
      color = "#003E19";
      break;
    case "80":
      color = "#7FB174";
      break;
    case "100":
      color = "#072A24";
      break;
    case "110":
      color = "#E81E25";
      break;
  }
  return color;
}

// 出库单状态
function colorByStatus3(orderStatus = 0) {
  // 00   0:默认;   #D1DDDB
  // 1   1:部分出库;   #85B8CB
  // 2   2:全部出库;   #1D6A96
  // console.log("orderStatus=======>"+typeof orderStatus)
  let color = "#D1DDDB";
  switch (orderStatus) {
    case "1":
      color = "#85B8CB";
      break;
    case "2":
      color = "#1D6A96";
      break;
  }
  return color;
}

// 出库单状态
function colorByPayStatus(orderStatus = "3") {
  // 00   0:默认;   #D1DDDB
  // 1   1:部分出库;   #85B8CB
  // 2   2:全部出库;   #1D6A96
  // console.log("orderStatus=======>"+typeof orderStatus)
  let color = "#7BC5AE";
  switch (orderStatus + "") {
    case "1":
      color = "#E72125";
      break;
    case "2":
      color = "#F47775";
      break;
    case "3":
      color = "#008A6C";
      break;
  }
  return color;
}

function download(
  url = "http:www.xxx.com/download?name=file.pdf",
  fileName = "未知文件"
) {
  const a = document.createElement("a");
  a.style.display = "none";
  // a.setAttribute('target', '_self');
  /*
   * download的属性是HTML5新增的属性
   * href属性的地址必须是非跨域的地址，如果引用的是第三方的网站或者说是前后端分离的项目(调用后台的接口)，这时download就会不起作用。
   * 此时，如果是下载浏览器无法解析的文件，例如.exe,.xlsx..那么浏览器会自动下载，但是如果使用浏览器可以解析的文件，比如.txt,.png,.pdf....浏览器就会采取预览模式
   * 所以，对于.txt,.png,.pdf等的预览功能我们就可以直接不设置download属性(前提是后端响应头的Content-Type: application/octet-stream，如果为application/pdf浏览器则会判断文件为 pdf ，自动执行预览的策略)
   */
  fileName && a.setAttribute("download", fileName);
  a.href = url;
  document.body.appendChild(a);
  a.click();
  document.body.removeChild(a);
}

function listIsEmpty(arr) {
  return !(arr && arr.length > 0);
}

function encode(value) {
  if (value) {
    value = value.replaceAll("\\n", "<br>");
    value = value.replaceAll("\n", "<br>");
  }
  return value;
}
function decode(value) {
  if (value) {
    value = value.replaceAll("<br>", "\n");
  }
  return value;
}
/**
 * 判断消息需要操作的页面和当前的页面是否是同一个
 * @param {消息需要操作的页面} operatePage
 */
function judgeOperateUrl(operatePage, sourcePage = "") {
  // console.log(`location.href:${location.href},operatePage:${operatePage},sourcePage:${sourcePage},location.href.includes(operatePage):${location.href.includes(operatePage)}`)
  if (sourcePage) {
    return operatePage.endsWith(sourcePage);
  } else if (location.href.includes(operatePage)) {
    // 当前消息需要操作的页面和当前页面一致，无需该页面处理
    let suffix = location.href.split(operatePage)[1];
    // 避免出现 /productmng/product 相似的情况 /productmng/product-brand
    if (suffix.startsWith("-")) return false;
    return true;
  }
  return false;
}

// 从字符串中获取数字
function selectNumberByStr(str) {
  if (!str) return undefined;
  let numbers = str.match(/\d+/g);
  if (numbers && numbers.length > 0) {
    return numbers[0];
  }
  return undefined;
}

// 匹配所有数字并在前面添加'num'
function addSplitPrefix(text, prefix) {
  return text.replace(/\d+[.]/g, prefix + "$&");
}

import { setCookie, getCookie } from "./cookie_util.js";
import { isDev, setIsDev } from "./my_env.js";
import {
  judgeFileType,
  objHasKey,
  listFindOne,
  list2listHandle,
  listToSet,
  playNotice,
  timeKeepM,
  smartSortOptimized,
} from "./chat/data_operation.js";

export default {
  my_trim, //   避免空串
  my_merge, //   多个字符串合并
  my_obj_merge, //   多个字符串合并，传过来是[{对象,'propertyName'}]集合
  isNull, //   字符串是否为空
  strShow, // 展示字符串，超过指定长度截取 val,len

  computer, //   数字计算   p1,p2   type: 0:加;1:减;2:乘;3:除;
  transferNaNToEmpty, //   值或对象中存在NaN，有则替换掉，返回正确对象
  timestampToTime, //   时间戳转时间
  my_calFinalPrice, //   商品列表  第几个item列计算   小计finalPrice

  getA_or_B, // 获取值，谁不为空 返回谁
  colorByStatus, // 根据订单状态orderStatus获取按钮颜色
  colorByStatus3,
  download, // 文件下载
  listIsEmpty, // 数组是否为空
  isDev, // 是否测试环境
  setIsDev, // 设置是否是dev环境，默认不是
  colorByPayStatus, // 是否测试环境

  setCookie, //
  getCookie, //
  encode, //
  decode, //
  judgeOperateUrl,
  myReplaceMany,
  addSplitPrefix,
  selectNumberByStr, // 从字符串中获取数字

  judgeFileType,
  objHasKey,
  listFindOne,
  list2listHandle,
  listToSet,
  playNotice,
  timeKeepM,
  smartSortOptimized, // 排序 list,属性,asc/desc
};
