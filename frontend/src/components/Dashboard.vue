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
                <v-card>
                  <v-data-table
                    :headers="headers"
                    :search="search"
                    :items="resources"
                    sort-by="resourceName"
                    item-key="resourceName"
                    show-expand
                    :single-expand="singleExpand"
                    :expanded.sync="expanded"
                  >
                    <template v-slot:top>
                      <v-text-field
                        v-model="search"
                        append-icon="mdi-magnify"
                        label="Search"
                        class="mx-10"
                      ></v-text-field>
                      <v-dialog
                        v-model="dialog"
                        max-width="500px"
                      >

                        <v-card>
                          <v-card-title>
                            <span class="headline">Book this resource</span>
                          </v-card-title>

                          <v-card-text>
                            <v-container>
                                  <v-form ref="form">
                                    <v-text-field
                                        label="Resource description"
                                        v-model="description">
                                    ></v-text-field>
                                    <v-text-field
                                        v-model="first"
                                        label="First name"
                                    ></v-text-field>
                                    <v-text-field
                                        v-model="last"
                                        label="Last name"
                                    ></v-text-field>
                                    <v-text-field
                                        v-model="reason"
                                        label="Reason of reserving the resource"
                                    ></v-text-field>
                                    <v-text-field
                                        v-model="time"
                                        label="Estimated reservation time"
                                    ></v-text-field>
                                    <v-text-field
                                        v-model="current_time"
                                        label="Current reservation time"
                                    ></v-text-field>
                                    <v-checkbox
                                        v-model="notify"
                                        label="Keep me notified"
                                    ></v-checkbox>
                                  </v-form>
                                
                            </v-container>
                          </v-card-text>

                          <v-card-actions>
                            <v-spacer></v-spacer>
                            <v-btn
                                color="blue darken-1"
                                text
                                @click="close"
                            >
                              Cancel
                            </v-btn>
                            <v-btn
                                color="blue darken-1"
                                text
                                @click="book"
                            >
                              Book
                            </v-btn>
                          </v-card-actions>
                        </v-card>
                      </v-dialog>
                    </template>
                    <template v-slot:[`item.actions`]="{ item }">
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
                    <template v-slot:expanded-item="{ headers, item }">
                      <td :colspan="headers.length">
                        More info about {{ item.resourceName }}
                      </td>
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
      description: '',
      first: '',
      last: '',
      reason: '',
      time: '',
      current_time: '',
      notify: '',

      search: '',
      dialog: false,
      dialogDelete: false,

      expanded: [],
      singleExpand: true,

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
        { text: 'Actions', value: 'actions', sortable: false },
        { text: '', value: 'data-table-expand' }
      ],

      resources: [
        { resourceName: "Resource 1", resourceDescription: "Description 1", resourceType: "ROOM", resourceState: "Available"},
        { resourceName: "Resource 2", resourceDescription: "Description 2", resourceType: "ROOM", resourceState: "Available"},
        { resourceName: "Resource 3", resourceDescription: "Description 3", resourceType: "ROOM", resourceState: "Available"},
        { resourceName: "Resource 4", resourceDescription: "Description 4", resourceType: "ROOM", resourceState: "Available"},
        { resourceName: "Resource 5", resourceDescription: "Description 5", resourceType: "ROOM", resourceState: "Available"},
      ],

      editedIndex: -1,
      editedItem: {
        resourceName: '',
        resourceDescription: '',
        resourceType: '',
        resourceState: ''
      },
      defaultItem: {
        resourceName: '',
        resourceDescription: '',
        resourceType: '',
        resourceState: ''
      },
    }),

    mounted: function() {
      this.username = localStorage.getItem('username');
      this.role = localStorage.getItem("role");
      this.organizationName = localStorage.getItem('organizationName');
    },

    computed: {
      formTitle () {
        return this.editedIndex === -1 ? 'New Item' : 'Book this resource'
      }
    },
    
    watch: {
      dialog (val) {
        val || this.close()
      },
      dialogDelete (val) {
        val || this.closeDelete()
      },
    },

    methods: {
      logout() {
        this.$router.push({ name: 'Login' });
      },

      bookItem(item) {
        this.editedIndex = this.resources.indexOf(item)
        this.editedItem = Object.assign({}, item)
        this.dialog = true
      },
      
      close () {
        this.dialog = false
        this.$nextTick(() => {
          this.editedItem = Object.assign({}, this.defaultItem)
          this.editedIndex = -1
        })
      },
      
      book () {
      }

    },

    created(){

    }
}
</script>

<style>



</style>