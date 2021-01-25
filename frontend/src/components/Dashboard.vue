<template>
    <v-container>
      <v-card
          class="mx-auto overflow-hidden"
      >
        <v-app-bar fixed color="deep-purple accent-4">
          <v-app-bar-nav-icon style="color:white;"></v-app-bar-nav-icon>
          <v-toolbar-title style="color:white;">Welcome, {{username}}</v-toolbar-title>
          <v-spacer></v-spacer>
          <v-toolbar-title style="color:white;">Organization: {{organizationName}}</v-toolbar-title>
          <v-menu bottom left>
            <template v-slot:activator="{ on, attrs }">
              <v-btn icon color="white" v-bind="attrs" v-on="on">
                <v-icon>mdi-dots-vertical</v-icon>
              </v-btn>
            </template>

            <v-list>
              <v-list-item>
                <v-list-item-title>
                  <v-btn @click="logout">
                    Logout
                  </v-btn>
                </v-list-item-title>
              </v-list-item>
            </v-list>
          </v-menu>
        </v-app-bar>

      </v-card>

        <v-card-text>
          <br><br><br>
            <div v-if = "role === 'ADMIN'">
                <v-card>
                    <v-tabs
                    background-color="deep-purple accent-4"
                    center-active
                    fixed-tabs
                    dark>
                        <v-tab>Users</v-tab>
                        <v-tab>Resources</v-tab>
                      <v-tab-item>
                        <Users />
                      </v-tab-item>
                      <v-tab-item>
                        <ResourcesAdmin/>
                      </v-tab-item>
                    </v-tabs>
                </v-card>
            </div>
            <div v-else>
              <ResourcesUser/>
            </div>
        </v-card-text>
    </v-container>
</template>

<script>
import ResourcesAdmin from "@/components/ResourcesAdmin.vue";
import Users from "@/components/Users";
import ResourcesUser from "@/components/ResourcesUser";

export default {
    name: 'Dashboard',
    components: {
      ResourcesAdmin,
      ResourcesUser,
      Users,
    },

    data: () => ({
      username: '',
      role: '',
      organizationName: '',
    }),

    mounted: function() {
      this.username = localStorage.getItem('username');
      this.role = localStorage.getItem("role");
      this.organizationName = localStorage.getItem('organizationName');
    },

    methods: {
      logout() {
        this.$router.push({ name: 'Login' });
      },
    },
}
</script>

<style>



</style>