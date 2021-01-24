<template>
  <v-data-table
      :headers="headers"
      :search="search"
      :items="users"
      sort-by="name"
      class="elevation-1"
  >
    <template v-slot:top>
      <v-toolbar flat >

        <v-dialog
            v-model="dialog"
            max-width="500px"
        >

          <v-card>
            <v-card-title>
              <span class="headline">{{ formTitle }}</span>
            </v-card-title>

            <v-card-text>
              <v-container>
                <v-row justify="center">
                  <v-col
                      cols="12"
                      sm="6"
                      md="4"
                  >
                    <v-select align='middle'
                              :items="permissions"
                              item-text="role"
                              item-value="role"
                              label="Permission"
                              persistent-hint
                              return-object
                              single-line
                              v-model="editedItem.role"
                    ></v-select>
                  </v-col>
                </v-row>
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
                  @click="save"
              >
                Save
              </v-btn>
            </v-card-actions>
          </v-card>
        </v-dialog>
        <v-dialog v-model="dialogDelete" max-width="500px">
          <v-card>
            <v-card-title class="justify-center font-weight-regular">Are you sure you want to delete this user?</v-card-title>
            <v-card-actions>
              <v-spacer></v-spacer>
              <v-btn color="blue darken-1" text @click="closeDelete">Cancel</v-btn>
              <v-btn color="blue darken-1" text @click="deleteItemConfirm">OK</v-btn>
              <v-spacer></v-spacer>
            </v-card-actions>
          </v-card>
        </v-dialog>
      </v-toolbar>
      <v-text-field
          v-model="search"
          label="Search"
          class="mx-10"
      ></v-text-field>
    </template>
    <template v-slot:[`item.actions`]="{ item }">
      <v-icon
          small
          class="mr-2"
          @click="editItem(item)"
      >
        mdi-pencil
      </v-icon>
      <v-icon
          small
          @click="deleteItem(item)"
      >
        mdi-delete
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
</template>

<script>
import axios from "axios";
const API_PATH = "http://localhost:8081/api";
export default {
  name: "Users.vue",
  computed: {
    formTitle () {
      return this.editedIndex === -1 ? 'New Item' : 'Edit user permissions'
    }
  },
  mounted() {
    this.getUsers()
  },
  methods: {
    getUsers() {
      axios.get(API_PATH + "/users")
          .then(response => {
            this.users = response.data;
          })
    },

    editItem (item) {
      this.editedIndex = this.users.indexOf(item)
      this.editedItem = Object.assign({}, item)
      this.dialog = true
    },

    deleteItem (item) {
      this.editedIndex = this.users.indexOf(item)
      this.editedItem = Object.assign({}, item)
      this.dialogDelete = true
    },

    deleteItemConfirm () {
      this.users.splice(this.editedIndex, 1)
      this.closeDelete()
    },

    close () {
      this.dialog = false
      this.$nextTick(() => {
        this.editedItem = Object.assign({}, this.defaultItem)
        this.editedIndex = -1
      })
    },

    closeDelete () {
      this.dialogDelete = false
      this.$nextTick(() => {
        this.editedItem = Object.assign({}, this.defaultItem)
        this.editedIndex = -1
      })
    },

    save () {
      if (this.editedIndex > -1) {
        Object.assign(this.users[this.editedIndex], this.editedItem)
      } else {
        this.users.push(this.editedItem)
      }
      axios.put(API_PATH + "/users/" + this.editedItem.id, {
            role: this.editedItem.role
          })
          .then((res) => {
            console.log(res);
            this.close();
          })
          .catch((error) => {
            console.log(error);
            this.close();
          });
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
  data: () => ({
    search: '',
    dialog: false,
    dialogDelete: false,
    permissions: ['VIEW', 'EDIT'],

    headers: [
      { text: 'Username', value: 'username', align: 'start', sortable: true },
      { text: 'First name', value: 'firstName', sortable: true },
      { text: 'Last name', value: 'lastName', sortable: true },
      { text: 'Permission', value: 'role' },
      { text: 'Actions', value: 'actions', sortable: false }
    ],

    users: [],

    editedIndex: -1,
    editedItem: {
      id: '',
      name: '',
      role: '',
      firstName: '',
      lastName: ''
    },
    defaultItem: {
      id: '',
      name: '',
      role: '',
      firstName: '',
      lastName: ''
    }
  })
}
</script>

<style scoped>

</style>