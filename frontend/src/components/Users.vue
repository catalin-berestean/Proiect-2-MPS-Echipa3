<template>
  <v-data-table
      :headers="headers"
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
                <v-row>
                  <v-col
                      cols="12"
                      sm="6"
                      md="4"
                  >
                    <v-text-field
                        v-model="editedItem.name"
                        label="User name"
                    ></v-text-field>
                  </v-col>
                  <v-col
                      cols="12"
                      sm="6"
                      md="4"
                  >
                    <v-text-field
                        v-model="editedItem.firstName"
                        label="First name"
                    ></v-text-field>
                  </v-col>
                  <v-col
                      cols="12"
                      sm="6"
                      md="4"
                  >
                    <v-text-field
                        v-model="editedItem.lastName"
                        label="Last name"
                    ></v-text-field>
                  </v-col>
                </v-row>
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
          @click="initialize"
      >
        Reset
      </v-btn>
    </template>
  </v-data-table>
</template>

<script>
export default {
  name: "Users.vue",
  computed: {
    formTitle () {
      return this.editedIndex === -1 ? 'New Item' : 'Edit Item'
    }
  },
  methods: {
    getUsers() {

    },
    getResources() {

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
      this.close()
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
    dialog: false,
    dialogDelete: false,
    permissions: [
      {role: 'VIEW'},
      {role: 'EDIT'}
    ],

    headers: [
      { text: 'Username', value: 'name', align: 'start', sortable: true },
      { text: 'First name', value: 'firstName', sortable: true },
      { text: 'Last name', value: 'lastName', sortable: true },
      { text: 'Permission', value: 'role' },
      { text: 'Actions', value: 'actions', sortable: false }
    ],

    users: [
      {name: 'user1', firstName: 'Ion', lastName: 'Popescu', role: 'vizualizare'},
      {name: 'user2', firstName: 'Ion', lastName: 'Popescu', role: 'vizualizare'},
      {name: 'user3', firstName: 'Ion', lastName: 'Popescu', role: 'rezervare'},
      {name: 'user4', firstName: 'Ion', lastName: 'Popescu', role: 'vizualizare'},
      {name: 'user3', firstName: 'Ion', lastName: 'Popescu', role: 'rezervare'},
      {name: 'user3', firstName: 'Ion', lastName: 'Popescu', role: 'rezervare'},
      {name: 'user3', firstName: 'Ion', lastName: 'Popescu', role: 'rezervare'},
      {name: 'user3', firstName: 'Ion', lastName: 'Popescu', role: 'rezervare'},
      {name: 'user3', firstName: 'Ion', lastName: 'Popescu', role: 'rezervare'}
    ],

    editedIndex: -1,
    editedItem: {
      name: '',
      role: ''
    },
    defaultItem: {
      name: '',
      role: ''
    }
  })
}
</script>

<style scoped>

</style>