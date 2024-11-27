import { defineStore } from 'pinia'

export const menuStore = defineStore('menuStore', {
    state: () => {
        return{
            collapse: false
        }
    },
    getters: {
   getCollapse(state){
    return state.collapse
   }
    },
    actions: {
      setCollapse(collapse: boolean){
        this.collapse= collapse
      }
    },
  })