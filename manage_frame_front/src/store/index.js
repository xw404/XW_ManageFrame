import {createStore} from 'vuex'
import router from '@/router'

export default createStore({
    //单页面有效放在state里面
    state: {
        hasRoutes: false,
        editableTabsValue: "/index",
        editableTabs: [
            {
                title: '首页',
                name: 'index'
            }
        ]
    },
    getters: {
        GET_TOKEN: state => {
            return sessionStorage.getItem("token")
        },
        GET_MENULIST: state => {
            return JSON.parse(sessionStorage.getItem("menuList"))
        },
        GET_USERINFO: state => {
            return JSON.parse(sessionStorage.getItem("userInfo"))
        }
    },
    mutations: {
        SET_TOKEN: (state, token) => {
            sessionStorage.setItem("token", token);
        },
        SET_MENULIST: (state, menuList) => {
            sessionStorage.setItem("menuList", JSON.stringify(menuList));
        },
        SET_USERINFO: (state, userInfo) => {
            sessionStorage.setItem("userInfo", JSON.stringify(userInfo));
        },
        SET_ROUTER_STATE: (state, hasRoutes) => {
            state.hasRoutes = hasRoutes;
        },
        SET_ADD_TABS: (state, tab) => {
            //tab不等于空进行如下操作
            if (tab && state.editableTabs.findIndex(e=>e.name===tab.path)===-1) {
                state.editableTabs.push({
                    title: tab.name,
                    name: tab.path
                })
            }
            //左侧菜单选中哪个就默认是哪个
            state.editableTabsValue = tab.path
        },
        RESET_TABS: (state) => {
            state.editableTabsValue = "/index",
                state.editableTabs = [
                    {
                        title: '首页',
                        name: 'index'
                    }
                ]
        },
    },
    actions: {
        //安全退出
        logout() {
            window.sessionStorage.clear();
            router.replace("/login")
        }
    },
    modules: {}
})
