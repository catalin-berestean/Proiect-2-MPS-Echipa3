<template>
  <v-data-table
    :headers="headers"
    :items="resources"
    sort-by="resName"
    class="elevation-1"
  >
    <template v-slot:top>
      <v-toolbar
        flat
      >
        <v-spacer></v-spacer>
        <v-dialog
          v-model="dialog"
          max-width="500px"
        >
          <template v-slot:activator="{ on, attrs }">
            <v-btn
              color="primary"
              dark
              class="mb-2"
              v-bind="attrs"
              v-on="on"
            >
              New Resource
            </v-btn>
          </template>
          <v-card>
            <v-card-title>
              <span class="headline">{{ formTitle }}</span>
            </v-card-title>

            <v-card-text>
              <v-container>
                <v-row justify="center">
                  <v-col>
                    <v-text-field
                      v-model="editedItem.resName"
                      label="Resource name"
                    ></v-text-field>
                  </v-col>
                </v-row>
                <v-row justify="center">
                  <v-col>
                    <v-text-field
                      v-model="editedItem.resDesc"
                      label="Description"
                    ></v-text-field>
                  </v-col>
                </v-row>
                <v-row justify="center">
                  <v-col>
                    <v-text-field
                      v-model="editedItem.resType"
                      label="Resource type"
                    ></v-text-field>
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
        
      </v-toolbar>
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
import axios from "axios";

const API_PATH = "http://localhost:8081/api";
export default {
  name: "ResourcesAdmin.vue",
  computed: {
    formTitle () {
      return this.editedIndex === -1 ? 'New Item' : 'Edit user permissions'
    }
  },
  data: () => ({
    dialog: false,
    dialogDelete: false,

    display_success: false,
    display_error: false,
    error_message: '',
    success_message: 'Resource was added successfully',
    countSpaces: 3,
    resourceName: '',
    resourceDescription: '',
    resourceType: '',

    watch: {
      dialog (val) {
        val || this.close()
      },
      dialogDelete (val) {
        val || this.closeDelete()
      },
    },

    headers: [
      { text: 'Name', value: 'resName', align: 'start', sortable: true },
      { text: 'Description', value: 'resDesc', sortable: true },
      { text: 'Type', value: 'resType', sortable: true }
    ],

    resources: [
      {resName: "resource 1", resDesc: "desc 1", resType: "ROOM"},
      {resName: "resource 2", resDesc: "desc 2", resType: "ROOM"},
      {resName: "resource 3", resDesc: "desc 3", resType: "ROOM"},
      {resName: "resource 4", resDesc: "desc 4", resType: "ROOM"}
    ],

    editedIndex: -1,
      editedItem: {
        resName: '',
        resDesc: '',
        resType: '',
      },
      defaultItem: {
        resName: '',
        resDesc: '',
        resType: '',
      },


  }),
  methods: {
    addNewResource() {
      axios
          .post(API_PATH + "/resources", {
            name: this.resourceName,
            description: this.resourceDescription,
            type: this.resourceType
          })
          .then((res) => {
            console.log(res);
            this.display_success = true;
          })
          .catch((error) => {
            let msg = error.response.data["message"]
            if(!msg) {
              this.error_message = "Error processing the request";
            } else {
              this.error_message = msg;
            }
            this.display_error = true;
          });
    },

    editItem (item) {
      this.editedIndex = this.desserts.indexOf(item)
      this.editedItem = Object.assign({}, item)
      this.dialog = true
    },

    deleteItem (item) {
      this.editedIndex = this.desserts.indexOf(item)
      this.editedItem = Object.assign({}, item)
      this.dialogDelete = true
    },

    deleteItemConfirm () {
      this.desserts.splice(this.editedIndex, 1)
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
        Object.assign(this.desserts[this.editedIndex], this.editedItem)
      } else {
        this.desserts.push(this.editedItem)
      }
      this.close()
    },
  }
}
</script>

<style scoped>

</style>