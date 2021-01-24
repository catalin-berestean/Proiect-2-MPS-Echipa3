<template>
    <v-container>
        <v-row justify="center">
            <div class="font-weight-regular display-1 pa-4">
              Welcome, {{username}}
            </div>
        </v-row>

        <v-card-text>
            <div v-if = "role === 'ADMIN'">
                <v-card>
                    <v-tabs
                    background-color="deep-purple accent-4"
                    center-active
                    fixed-tabs
                    dark
                    >
                        <v-tab>Users</v-tab>
                        <v-tab>Resources</v-tab>
                        
                        <v-tab-item>
                          <Users />
                        </v-tab-item>
                      <v-tab-item>
                        <Resources/>
                      </v-tab-item>
                    </v-tabs>
                </v-card>
                <v-spacer></v-spacer>
                <br v-for="c in countSpaces" :key="c">
                <v-row>
                    <v-col align="center">
                        <v-btn
                            large
                            raised
                            rounded
                            color="primary"
                            @click="logout"
                            >Logout</v-btn>
                    </v-col>
                </v-row>  
            </div>
            <div v-else>
                <v-card>
                    <h1>User normal</h1>
                </v-card>                

                <v-row align="center" justify="center">
                    <v-col align="center">
                        <v-btn
                            large
                            raised
                            color="primary"
                            @click="logout"
                            >Logout</v-btn>
                    </v-col>
                </v-row>           
            </div>  
        </v-card-text>
        
        
    </v-container>
</template>

<script>
import Resources from "@/components/Resources.vue";
import Users from "@/components/Users";
export default {
    name: 'Dashboard',
    components: {
      Resources,
      Users
    },
    data: () => ({
        countSpaces: 2,
        username: '',
        role: '',
    }),

    mounted: function() {
        if (localStorage.getItem('username')) {
            try {
                this.username = localStorage.getItem('username');
                this.role = localStorage.getItem("role");
            } catch(e) {
                localStorage.removeItem('username');
            }
        }
    },
    methods: {
      logout() {
        this.$router.push({ name: 'Login' });
      }
    },

    created(){

    }
}
</script>

<style>



</style>