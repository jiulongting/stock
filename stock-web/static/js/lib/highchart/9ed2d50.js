(window.webpackJsonp=window.webpackJsonp||[]).push([[32,13,52],{1025:function(t,e,c){"use strict";c.r(e);c(27),c(47);var n={props:{isSmall:{type:Boolean,default:!0},src:{type:String,default:""},width:{type:String,default:""},height:{type:String,default:""},error:{type:String,default:""}},data:function(){return{images:[],image:"",isShowBigImage:!1,loading:!0}},computed:{cssStyle:function(){var t="".concat(this.width),e="".concat(this.height);return{width:t=t.indexOf("%")>0?t:t.replace("px","")+"px",height:e=e.indexOf("%")>0?e:e.replace("px","")+"px"}},errorSrc:function(){return this.error?this.error:"https://jiucaigongshe.oss-cn-beijing.aliyuncs.com/PC/empty.png"}},methods:{showMyImg:function(t){t.target&&this.$imageViewer(t,t.target.src)},onError:function(){this.image=this.src},onLoad:function(){this.loading=!1}},watch:{src:function(){if(this.src.indexOf("oss")>-1)return this.image=this.src+"?x-oss-process=image/quality,q_60";this.image=this.src}},mounted:function(){if(this.src.indexOf("oss")>-1)return this.image=this.src+"?x-oss-process=image/quality,q_60";this.image=this.src}},o=(c(1027),c(8)),component=Object(o.a)(n,(function(){var t=this,e=t.$createElement,c=t._self._c||e;return c("div",{style:t.cssStyle},[c("img",{class:{op0:!t.isSmall},style:t.cssStyle,attrs:{src:t.src},on:{click:function(e){return e.stopPropagation(),t.showMyImg.apply(null,arguments)},error:function(e){return t.onError()},load:function(e){return t.onLoad()}}})])}),[],!1,null,"ac81fa18",null);e.default=component.exports},1026:function(t,e,c){},1027:function(t,e,c){"use strict";c(1026)},1030:function(t,e,c){"use strict";var n=c(65),o=(c(16),c(18),c(91),c(32),c(92),"[object Object]"),r="[object Array]";e.a={CopyObject:function(t){var e=this,c={};return"object"!==Object(n.a)(t)?t:(Object.keys(t).forEach((function(n){var l=t[n];switch(e.getType(l)){case o:c[n]=e.CopyObject(l);break;case r:c[n]=[],l.forEach((function(t,o){c[n].push(e.CopyObject(t))}));break;default:c[n]=l}})),c)},CopyArray:function(t){var e=this;return t.map((function(t){return e.CopyObject(t)}))},deepCopy:function(t){return"[object Object]"===t.toString()?this.CopyObject(t):this.CopyArray(t)},getType:function(t){return Object.prototype.toString.call(t)}}},1069:function(t,e,c){},1093:function(t,e,c){"use strict";c(1069)},1129:function(t,e,c){},1176:function(t,e,c){"use strict";var n=c(12),o=c(156),r=c(66),l=c(49),d=c(31),h=c(14),f=c(1177),v=c(298),m=c(1178),_=c(1179),k=c(155),C=c(1180),w=[],y=w.sort,x=h((function(){w.sort(void 0)})),D=h((function(){w.sort(null)})),S=v("sort"),A=!h((function(){if(k)return k<70;if(!(m&&m>3)){if(_)return!0;if(C)return C<603;var code,t,e,c,n="";for(code=65;code<76;code++){switch(t=String.fromCharCode(code),code){case 66:case 69:case 70:case 72:e=3;break;case 68:case 71:e=4;break;default:e=2}for(c=0;c<47;c++)w.push({k:t+c,v:e})}for(w.sort((function(a,b){return b.v-a.v})),c=0;c<w.length;c++)t=w[c].k.charAt(0),n.charAt(n.length-1)!==t&&(n+=t);return"DGBEFHACIJK"!==n}}));n({target:"Array",proto:!0,forced:x||!D||!S||!A},{sort:function(t){void 0!==t&&o(t);var e=r(this);if(A)return void 0===t?y.call(e):y.call(e,t);var c,n,h=[],v=l(e.length);for(n=0;n<v;n++)n in e&&h.push(e[n]);for(c=(h=f(h,function(t){return function(e,c){return void 0===c?-1:void 0===e?1:void 0!==t?+t(e,c)||0:d(e)>d(c)?1:-1}}(t))).length,n=0;n<c;)e[n]=h[n++];for(;n<v;)delete e[n++];return e}})},1177:function(t,e){var c=Math.floor,n=function(t,e){var l=t.length,d=c(l/2);return l<8?o(t,e):r(n(t.slice(0,d),e),n(t.slice(d),e),e)},o=function(t,e){for(var element,c,n=t.length,i=1;i<n;){for(c=i,element=t[i];c&&e(t[c-1],element)>0;)t[c]=t[--c];c!==i++&&(t[c]=element)}return t},r=function(t,e,c){for(var n=t.length,o=e.length,r=0,l=0,d=[];r<n||l<o;)r<n&&l<o?d.push(c(t[r],e[l])<=0?t[r++]:e[l++]):d.push(r<n?t[r++]:e[l++]);return d};t.exports=n},1178:function(t,e,c){var n=c(134).match(/firefox\/(\d+)/i);t.exports=!!n&&+n[1]},1179:function(t,e,c){var n=c(134);t.exports=/MSIE|Trident/.test(n)},1180:function(t,e,c){var n=c(134).match(/AppleWebKit\/(\d+)\./);t.exports=!!n&&+n[1]},1181:function(t,e,c){"use strict";c(1129)},980:function(t,e,c){"use strict";c.r(e);c(91);var n=c(85),object=c(1030),o={mixins:[c(33).a],props:{name:{type:[String]},alist:{type:[Array],default:function(){return[]}},id:{type:[String]},active:{type:[String]},ishow:{type:[Boolean]}},data:function(){return{checkPrice:0,checkRange:0,checkTime:0,list:[]}},watch:{ishow:{handler:function(t,e){t&&this.showZk()},deep:!0},alist:{handler:function(t,e){t&&(this.list=object.a.CopyArray(this.alist),this.showZk())},deep:!0}},mounted:function(){this.alist.length>0&&(this.list=object.a.CopyArray(this.alist),this.showZk())},methods:{showZk:function(){var t=this;this.list&&this.list.length>0&&this.list.map((function(e,i){if(e.article&&e.article.action_info){var c=t;t.$nextTick((function(){var t=e.article.article_id;1*$("#expound".concat(t)).height()>64?c.$set(e,"isShowHile",1):c.$set(e,"isShowHile",0)}))}}))},toggerLike:function(t){var e=this;if(this.user){var c=this.list[t],o=0==c.article.is_like?1:0;n.h.setLike({article_id:c.article.article_id,status:o}).then((function(c){e.list[t].article.is_like=o,0==o?e.list[t].article.like_count--:e.list[t].article.like_count++,e.toast(c.msg)})).catch((function(t){e.showError(t.message)}))}else this.$store.commit("setShowLogin",!0)},toggerStep:function(t){var e=this,c=this.list[t],o=0==c.article.is_step?1:0;n.h.setStep({article_id:c.article.article_id,status:o}).then((function(c){e.list[t].article.is_step=o,0==o?e.list[t].article.step_count--:e.list[t].article.step_count++,e.toast(c.msg)})).catch((function(t){e.showError(t.message)}))},setActiveSort:function(t,e){var c=0;return e?"asc"==e?c=2==t?0:2:"desc"==e&&(c=1==t?0:1):c=0==t?1:1==t?2:0,c},orderByPrice:function(t,e){var c=0,n=0;"price"==t?(c=this.checkPrice,this.checkPrice=this.setActiveSort(c,e),this.checkRange=0,this.checkTime=0,n=this.checkPrice):"range"==t?(c=this.checkRange,this.checkRange=this.setActiveSort(c,e),this.checkPrice=0,this.checkTime=0,n=this.checkRange):"time"==t&&(c=this.checkTime,this.checkTime=this.setActiveSort(c,e),this.checkPrice=0,this.checkRange=0,n=this.checkTime),this.$emit("update",{type:t,sort:n})}}},r=(c(1093),c(8)),component=Object(r.a)(o,(function(){var t=this,e=t.$createElement,c=t._self._c||e;return t.list&&t.list.length>0?c("div",{staticClass:"table-box"},[c("div",{staticClass:"th-box hsh-flex-both"},[c("div",{staticClass:"th"},[t._v("股票名称")]),t._v(" "),c("div",{staticClass:"th"},[c("span",{staticClass:"click",on:{click:function(e){return t.orderByPrice("price")}}},[t._v("最新价")]),t._v(" "),c("span",{staticClass:"arrow-box"},[c("i",{staticClass:"arrow ascending",class:{"asc-check":2==t.checkPrice},on:{click:function(e){return t.orderByPrice("price","asc")}}}),t._v(" "),c("i",{staticClass:"arrow descending",class:{"desc-check":1==t.checkPrice},on:{click:function(e){return t.orderByPrice("price","desc")}}})])]),t._v(" "),c("div",{staticClass:"th"},[c("span",{staticClass:"click",on:{click:function(e){return t.orderByPrice("range")}}},[t._v("涨跌幅")]),t._v(" "),c("span",{staticClass:"arrow-box"},[c("i",{staticClass:"arrow ascending",class:{"asc-check":2==t.checkRange},on:{click:function(e){return t.orderByPrice("range","asc")}}}),t._v(" "),c("i",{staticClass:"arrow descending",class:{"desc-check":1==t.checkRange},on:{click:function(e){return t.orderByPrice("range","desc")}}})])]),t._v(" "),c("div",{staticClass:"th"},[c("span",{staticClass:"click",on:{click:function(e){return t.orderByPrice("time")}}},[t._v("涨停时间")]),t._v(" "),c("span",{staticClass:"arrow-box"},[c("i",{staticClass:"arrow ascending",class:{"asc-check":2==t.checkTime},on:{click:function(e){return t.orderByPrice("time","asc")}}}),t._v(" "),c("i",{staticClass:"arrow descending",class:{"desc-check":1==t.checkTime},on:{click:function(e){return t.orderByPrice("time","desc")}}})])]),t._v(" "),c("div",{staticClass:"th"},[t._v("解析")])]),t._v(" "),c("ul",{staticClass:"td-box"},t._l(t.list,(function(e,i){return c("li",{key:e.article_id,staticClass:"row drvi straight-line"},[c("div",{staticClass:"hsh-flex-both alcenter",on:{click:function(c){return t.toArticleDetail(e.article.article_id)}}},[c("div",{staticClass:"hsh-flex-many-center td"},[c("div",{staticClass:"shrink fs15-bold"},[t._v(t._s(e.name))]),t._v(" "),c("div",{staticClass:"shrink fs12-bold-ash force-wrap"},[t._v(t._s(e.code))])]),t._v(" "),c("div",{staticClass:"hsh-flex-many-center td"},[c("div",{staticClass:"shrink number"},[t._v("\n              "+t._s(t.formatNumber(e.article.action_info.price))+"\n            ")]),t._v(" "),e.article.action_info.num?c("div",{staticClass:"sort"},[t._v("\n              "+t._s(e.article.action_info.num)+"\n            ")]):t._e()]),t._v(" "),c("div",{staticClass:"hsh-flex-upDown td"},[c("div",{staticClass:"shrink ",class:e.article.action_info.shares_range>=0?"cred":"cgreen"},[t._v("\n              "+t._s(t.formatNumber(e.article.action_info.shares_range))+"%\n            ")])]),t._v(" "),c("div",{staticClass:"hsh-flex-upDown td"},[c("div",{staticClass:"shrink fs15"},[t._v("\n              "+t._s(e.article.action_info.time?e.article.action_info.time:"--")+"\n            ")])]),t._v(" "),e.article.action_info?c("div",{staticClass:"td ",on:{click:function(t){t.stopPropagation()}}},[c("nuxt-link",{staticClass:"color-444",attrs:{target:"_blank",to:"/a/"+e.article.article_id},on:{click:function(t){t.stopPropagation()}}},[c("pre",{staticClass:"pre tl  hilll",attrs:{id:"expound"+e.article.article_id}},[t._v(t._s(e.article.action_info.expound))])]),t._v(" "),c("pre",{staticClass:"pre tl expound ",class:1==e.isShowHile?"threeLine":2==e.isShowHile?"noneHile":"threeLine"},[t._v(" "),c("nuxt-link",{staticClass:"color-444",attrs:{target:"_blank",to:"/a/"+e.article.article_id},on:{click:function(t){t.stopPropagation()}}},[t._v(t._s(e.article.action_info.expound))]),2==e.isShowHile?c("span",{staticClass:"showTipClose click",on:{click:function(t){t.stopPropagation(),e.isShowHile=1}}},[t._v("收起")]):t._e()],1),t._v(" "),1==e.isShowHile?c("div",{staticClass:"showTip click",on:{click:function(t){t.stopPropagation(),e.isShowHile=2}}},[t._v("\n              ...展开\n            ")]):t._e()],1):t._e()]),t._v(" "),c("div",{staticClass:"hsh-flex-right"},[c("div",{staticClass:"handle-box hsh-flex-both"},[c("div",{staticClass:"handle click",on:{click:function(c){return c.stopPropagation(),t.toArticleDetail(e.article.article_id+"?type=forward")}}},[c("i",{staticClass:"icon forward lf"}),t._v(" "),c("span",{staticClass:"fs14-ash lf"},[t._v(t._s(e.article.forward_count))])]),t._v(" "),c("div",{staticClass:"handle click",on:{click:function(c){return c.stopPropagation(),t.toArticleDetail(e.article.article_id+"?type=comment")}}},[c("i",{staticClass:"icon reply lf"}),t._v(" "),c("span",{staticClass:"fs14-ash lf"},[t._v(t._s(e.article.comment_count))])]),t._v(" "),c("div",{staticClass:"handle click",class:{active:e.article.is_like},on:{click:function(e){return e.stopPropagation(),t.toggerLike(i)}}},[c("i",{staticClass:"icon fabulous lf"}),t._v(" "),c("span",{staticClass:"fs14-ash lf"},[t._v(t._s(e.article.like_count))])]),t._v(" "),c("div",{staticClass:"handle click",class:{active:e.article.is_step},on:{click:function(e){return e.stopPropagation(),t.toggerStep(i)}}},[c("i",{staticClass:"icon tread lf"}),t._v(" "),c("span",{staticClass:"fs14-ash lf"},[t._v(t._s(e.article.step_count))])])])])])})),0)]):t._e()}),[],!1,null,"63c603c6",null);e.default=component.exports},993:function(t,e,c){"use strict";c.r(e);var n=c(28),o=(c(112),c(46),c(91),c(1176),c(27),c(74),c(1025),c(85)),r=c(980),l=c(0),d=c.n(l),h=c(33),f=c(154),v=c(281),m=c(2),_={layout:"first",mixins:[h.a],components:{ActionList:r.default,EwmShareWechat:v.default},head:function(){return{title:"韭研公社-".concat(this.selectDate,"异动"),link:[]}},data:function(){return{wechatDialog:!1,right:0,recommendCount:0,allCount:0,scrollTop:0,sort:0,sortType:0,activeName:"all",selectDate:"",apiDate:"",actionMap:{},actionFieldList:[],recommendActionList:null,pageNo:0,loading:!1,totalCount:"",list:[],isLastPage:!1,src:""}},asyncData:function(t){return Object(n.a)(regeneratorRuntime.mark((function e(){var c,n,r,l,h,v,_,k,C,w,y,x,D,S,A,N,P,L,$,data,j,T;return regeneratorRuntime.wrap((function(e){for(;;)switch(e.prev=e.next){case 0:return t.app.$cookies.get("SESSION")&&(c=t.app.$cookies.get("SESSION"),f.a.setSession(c)),t.store.dispatch("getRankList",t),n=t.route.params.date?t.route.params.date:t.$formatDate(new Date,10),/^\d{4}-\d{1,2}-\d{1,2}/.test(n)||(n=t.$formatDate(new Date,10)),"Invalid Date"==new Date(n)&&(n=t.$formatDate(new Date,10),t.redirect("/action")),r="all",l=0,h=0,v="",_=1,k=0,C=0,w=[],[],y=0,x=!1,D=[],S=!1,A=(new Date).getTime(),N=d()(A),r=N.get("hour")<20&&N.get("hour")>=9?"all":"diagram",e.next=23,t.app.ApiManager.post({url:"/api/v1/action/count-pc",params:{date:n},headers:m.a});case 23:if(0!=(P=e.sent).errCode){e.next=43;break}if(l=P.data.recommend,h=P.data.all,v=P.data.date,n=P.data.date,"all"===r){e.next=38;break}return L={action_field_id:"recommend,"+v,pc:1,start:_,limit:30,sort_price:"price"===k?C:0,sort_range:"range"===k?C:0,sort_time:"time"===k?C:0},e.next=33,o.d.getActionList(L);case 33:$=e.sent,(data=$.data)&&(data.length,w=w.concat(data),y=data.length,x=!1),e.next=43;break;case 38:return e.next=40,o.d.getActionField({date:v,pc:1});case 40:j=e.sent,(T=j.data)&&(D=T.map((function(t){return t.isshow=!1,t})));case 43:return e.abrupt("return",{isLastPage:S,recommendCount:l,allCount:h,apiDate:v,selectDate:n,activeName:r,pageNo:_,recommendActionList:w,actionFieldList:D,totalCount:y,loading:x});case 44:case"end":return e.stop()}}),e)})))()},mounted:function(){var t,e,c=this;if(this.$store.state.rankList&&null!==(t=this.$store.rankData)&&void 0!==t&&t.hot_search_list&&null!==(e=this.$store.rankData)&&void 0!==e&&e.hot_article_list||this.$store.dispatch("getRankList"),"diagram"==this.activeName){var n={date:this.selectDate};this._sendPostRequest("/api/v1/action/diagram-url",n).then((function(t){c.src=t.data||""}))}},destroyed:function(){},watch:{$route:function(t,e){this.getStatus(this.$route.path)},activeName:function(t){var e=this;if("diagram"==t){var c={date:this.selectDate};this._sendPostRequest("/api/v1/action/diagram-url",c).then((function(t){e.src=t.data||""}))}}},methods:{shareWx:function(){this.wechatDialog=!0},createdQrcodes:function(){new QRCode(document.getElementById("qrcodes"),{width:180,height:180,text:"https://youjiangyun.oss-cn-shenzhen.aliyuncs.com/D9133B54-C839-4299-9E27-54F91A7A2C6C.png"})},changeTime:function(){this.$router.push("/action/"+this.selectDate)},initData:function(){var t=this;this.pageNo=0,this.totalCount="",this.sortType=0,this.sort="",this.recommendActionList=[],this.actionFieldList=[],o.d.getActionCount({date:this.selectDate}).then((function(e){t.recommendCount=e.data.recommend,t.allCount=e.data.all,t.apiDate=e.data.date,t.selectDate=t.apiDate,t.getData()})).catch((function(t){console.log(t)}))},getStatus:function(t){var e=t.split("/");return e[e.length-1]},getData:function(){this.loading=!0,"recommend"==this.activeName?(this.pageNo+=1,this.listRecommendAction()):"all"==this.activeName&&(this.actionFieldList=[],this.listAllAction())},load:function(){},listAllAction:function(){var t=this;o.d.getActionField({date:this.apiDate,pc:1}).then((function(e){e.data.map((function(e){t.$set(e,"isshow",!1)})),t.actionFieldList=e.data}))},listRecommendAction:function(){var t=this,e=this.sortType,c=this.sort,n={action_field_id:"recommend,"+this.apiDate,pc:1,start:this.pageNo,limit:30,sort_price:"price"===e?c:0,sort_range:"range"===e?c:0,sort_time:"time"===e?c:0};o.d.getActionList(n).then((function(e){e.data.length,t.recommendActionList=t.recommendActionList.concat(e.data),t.totalCount=e.data.length,t.loading=!1}))},listAction:function(t,i){var e=this,c=this.sortType,n=this.sort,r={action_field_id:t,sort_price:"price"===c?n:0,sort_range:"range"===c?n:0,sort_time:"time"===c?n:0,pc:1,start:1,limit:999};o.d.getActionList(r).then((function(t){e.$nextTick((function(){e.actionFieldList[i].list=t.data}))}))},showAllField:function(t,i){var e=this;this.actionFieldList.map((function(t,c){e.$set(t,"isshow",!t.isshow)})),this.$nextTick((function(){var c=e.$el.querySelector("#f".concat(t));window.scrollTo({top:c.offsetTop,behavior:"smooth"})}))},updateSort:function(t,e,c){this.sortType=t.type,this.sort=t.sort,"all"===this.activeName?this.listAction(e,c):"recommend"==this.activeName&&(this.loading=!0,this.recommendActionList=[],this.pageNo=1,this.listRecommendAction())}}},k=(c(1181),c(8)),component=Object(k.a)(_,(function(){var t=this,e=t.$createElement,c=t._self._c||e;return c("div",{directives:[{name:"infinite-scroll",rawName:"v-infinite-scroll",value:t.load,expression:"load"}],staticClass:"action-main",attrs:{"infinite-scroll-disabled":"disabled"}},[c("div",{staticClass:"transaction-box yd-box"},[c("div",{staticClass:"yd-top hsh-flex-upDown straight-line"},[c("div",{staticClass:"date-box"},[c("el-date-picker",{attrs:{editable:!1,"value-format":"yyyy-MM-dd",format:"MM月dd日",type:"date"},on:{change:t.changeTime},model:{value:t.selectDate,callback:function(e){t.selectDate=e},expression:"selectDate"}})],1),t._v(" "),c("div",{staticClass:"yd-tabs_item is-top",class:{"is-active":"all"==t.activeName},on:{click:function(e){t.activeName="all",t.initData()}}},[c("div",[t._v("全部异动解析")]),t._v(" "),c("div",{staticClass:"jc-count"},[t._v("\n          "+t._s("all"==t.activeName?"· "+t.allCount:"")+"\n        ")])]),t._v(" "),c("div",{staticClass:"yd-tabs_item is-top",class:{"is-active":"recommend"==t.activeName},on:{click:function(e){t.activeName="recommend",t.initData()}}},[c("div",[t._v("一字异动解析")]),t._v(" "),c("div",{directives:[{name:"show",rawName:"v-show",value:"recommend"==t.activeName,expression:"activeName == 'recommend'"}],staticClass:"jc-count"},[t._v("\n          · "+t._s(t.recommendCount)+"\n        ")])]),t._v(" "),c("div",{staticClass:"yd-tabs_item is-top",class:{"is-active":"diagram"==t.activeName},on:{click:function(e){t.activeName="diagram"}}},[c("div",[t._v("涨停简图")])])]),t._v(" "),"all"==t.activeName?c("section",[c("div",{staticClass:"tip-box"},[t._v("\n        内容全由人工编写，更新时间为交易日12:00、15:30前；团队辛苦整理，禁止商业转载\n      ")]),t._v(" "),c("ul",{staticClass:"module-box jc0"},t._l(t.actionFieldList,(function(e,n){return c("li",{key:e.action_field_id,staticClass:"module"},[e.action_field_id?c("div",{staticClass:"hsh-flex-upDown jc-bline",attrs:{id:"f"+e.action_field_id},on:{click:function(c){return t.showAllField(e.action_field_id,n)}}},[c("div",{staticClass:"sort-box"},[c("div",{staticClass:"parent count-filed"},[c("div",{staticClass:"fs18-bold lf"},[t._v(t._s(e.name))]),t._v(" "),c("div",{staticClass:"number lf"},[t._v(t._s(e.count))])]),t._v(" "),c("div",{directives:[{name:"show",rawName:"v-show",value:e.reason&&e.isshow,expression:"field.reason && field.isshow"}],staticClass:"mtb8 fs16 text-justify"},[c("span",{staticClass:"fs16-bold"},[t._v("题材：")]),t._v(t._s(e.reason)+"\n              ")])]),t._v(" "),c("i",{staticClass:"ml_caret",class:e.isshow?"el-icon-caret-top":"el-icon-caret-bottom"})]):t._e(),t._v(" "),c("div",{class:e.isshow?"":"hidden"},[c("action-list",{key:e.action_field_id,attrs:{"page-type":"action",ishow:e.isshow,name:e.name,alist:e.list},on:{"update:ishow":function(c){return t.$set(e,"isshow",c)},update:function(c){return t.updateSort(arguments[0],e.action_field_id,n)}}})],1)])})),0)]):t._e(),t._v(" "),"recommend"==t.activeName?c("section",[c("div",{staticClass:"tip-box"},[t._v("\n        该栏仅为复盘详细解析，无任何买卖推荐的意思，请注意风险；该栏内容在发布后，可能会持续更新\n      ")]),t._v(" "),c("action-list",{key:"recommend",attrs:{"page-type":"action",alist:t.recommendActionList},on:{update:t.updateSort}})],1):t._e(),t._v(" "),"diagram"==t.activeName?c("section",[t.src?c("div",{staticClass:"diagram",attrs:{id:"QR-code"}},[c("jc-image",{attrs:{width:"100%",height:"100%",src:t.src,alt:""}}),t._v(" "),c("div",{staticClass:"options"},[c("div",{staticClass:"handle click",on:{click:t.shareWx}},[c("i",{staticClass:"icon share "}),t._v(" "),c("span",{},[t._v("分享到微信")])]),t._v(" "),c("div",{staticClass:"handle click"},[c("a",{attrs:{href:t.src}},[t._v("保存图片")])])])],1):t._e()]):t._e()]),t._v(" "),t.wechatDialog?c("ewm-share-wechat",{attrs:{type:"action",date:t.selectDate},on:{close:function(e){t.wechatDialog=!1}}}):t._e()],1)}),[],!1,null,"15040e9e",null);e.default=component.exports;installComponents(component,{JcImage:c(1025).default})}}]);