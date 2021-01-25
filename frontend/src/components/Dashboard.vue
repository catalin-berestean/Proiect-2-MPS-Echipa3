<template>
    <v-container>
      <v-card
          class="mx-auto overflow-hidden"
      >
        <v-app-bar fixed>
          <v-app-bar-nav-icon></v-app-bar-nav-icon>
          <v-toolbar-title>Welcome, {{username}}</v-toolbar-title>
          <v-spacer></v-spacer>
          <v-toolbar-title>Organization: {{organizationName}}</v-toolbar-title>
          <v-menu bottom left>
            <template v-slot:activator="{ on, attrs }">
              <v-btn icon color="black" v-bind="attrs" v-on="on">
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
                <v-card>
                  <v-data-table
                    :headers="headers"
                    :search="search"
                    :items="resources"
                    sort-by="name"
                  >
                    <template v-slot:top>
                      <v-text-field
                        v-model="search"
                        append-icon="mdi-magnify"
                        label="Search"
                        class="mx-10"
                      ></v-text-field>
                    </template>
                    <template v-slot:[`item.actions`]="{ item }">
                      <v-icon
                          class="mr-2"
                          @click="showItemInfo(item)"
                      >
                        mdi-information-outline
                      </v-icon>
                      <v-icon v-if= "role === 'EDIT'"
                          @click="bookItem(item)"
                      >
                        mdi-calendar-edit
                      </v-icon>
                      <v-icon v-else
                          @click="bookItem(item)"
                          disabled
                      >
                        mdi-calendar-edit
                      </v-icon>
                    </template>
                    <template v-slot:no-data>
                      <v-btn
                          color="primary"
                          @click="getUsers"
                      >
                        Reset
                      </v-btn>
                    </template>
                  </v-data-table>
                </v-card>
            </div>
        </v-card-text>
    </v-container>
</template>

<script>
import ResourcesAdmin from "@/components/ResourcesAdmin.vue";
import Users from "@/components/Users";
export default {
    name: 'Dashboard',
    components: {
      ResourcesAdmin,
      Users
    },
    data: () => ({
      search: '',
      dialog: false,
      dialogDelete: false,

      drawer: false,
      group: null,
      countSpaces: 2,
      username: '',
      role: '',
      organizationName: '',

      headers: [
        { text: 'Resource name', value: 'resourceName', align: 'start', sortable: true },
        { text: 'Description', value: 'resourceDescription' },
        { text: 'Type', value: 'resourceType' },
        { text: 'State', value: 'resourceState' },
        { text: 'Actions', value: 'actions', sortable: false }
      ],

      resources: [
        { resourceName: "Resource 1", resourceDescription: "Description 1", resourceType: "ROOM", resourceState: "Available"},
        { resourceName: "Resource 2", resourceDescription: "Description 2", resourceType: "ROOM", resourceState: "Available"},
        { resourceName: "Resource 3", resourceDescription: "Description 3", resourceType: "ROOM", resourceState: "Available"},
        { resourceName: "Resource 4", resourceDescription: "Description 4", resourceType: "ROOM", resourceState: "Available"},
        { resourceName: "Resource 5", resourceDescription: "Description 5", resourceType: "ROOM", resourceState: "Available"},
      ],
    }),

    mounted: function() {
      this.username = localStorage.getItem('username');
      this.role = localStorage.getItem("role");
      this.organizationName = localStorage.getItem('organizationName');
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