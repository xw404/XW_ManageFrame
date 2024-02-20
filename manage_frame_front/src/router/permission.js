
import router from "@/router/index"
import store from "@/store"

//（动态路由主要文件）
//路由守卫（跳转到首页）
router.beforeEach((to,from,next)=>{
    const whiteList=['/login'] // 白名单
    let token=store.getters.GET_TOKEN;
    let hasRoutes = store.state.hasRoutes;
    let menuList = store.getters.GET_MENULIST;
    if(token){
        //如果没有加载过路由，就先加载路由
        if (!hasRoutes){
            bindRoute(menuList);
            store.commit("SET_ROUTER_STATE",true);
        }
        next();
    }else{
        if(whiteList.includes(to.path)){
            next();
        }else{
            next("/login");
        }
    }
})
//动态绑定路由
const bindRoute= (menuList)=>{
    //vue-route底层实现（TODO 原理不是很清楚2023-12-31）
    let newRoutes = router.options.routes;
    menuList.forEach(menu=>{
        if(menu.children){
            //遍历子菜单，转换为路由
            menu.children.forEach(m =>{
                //传入子菜单和父菜单名称
                let route = menuToRoute(m,menu.name)
                //存在路由，添加到newRoutes
                if(route){
                    newRoutes[0].children.push(route);
                }
            })
        }
    })
    //重新添加到路由管理当中
    newRoutes.forEach(route =>{
        router.addRoute(route)
    })
}

//菜单对象转换为路由对象
const menuToRoute=(menu,parentName)=>{
    //组件不为空
    if(!menu.component){
        return null;
    }else {
        let route = {
            name:menu.name,
            path:menu.path,
            meta:{
                parentName:parentName
            }
        }
        route.component=()=>import('@/views/'+menu.component+'.vue')
        return route;
    }
}

